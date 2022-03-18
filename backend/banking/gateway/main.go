package main

import (
	"context"
	"io"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	ginlogger "github.com/gin-contrib/logger"
	"github.com/gin-gonic/gin"
	"github.com/grpc-ecosystem/grpc-gateway/v2/runtime"
	"github.com/kelseyhightower/envconfig"
	"github.com/rs/zerolog"
	"github.com/rs/zerolog/log"
	"golang.org/x/sync/errgroup"
	"golang.org/x/xerrors"
	"google.golang.org/grpc"
	"google.golang.org/grpc/metadata"

	pb "github.com/kzmake/grpcinscala/api/gen/go/banking/v1"
)

type Env struct {
	GatewayAddress string `default:"0.0.0.0:8080"`
	ServiceAddress string `default:"localhost:50051"`
}

const envprefix = "GATEWAY"

var env Env

func init() {
	log.Logger = zerolog.New(zerolog.ConsoleWriter{Out: os.Stdout}).With().Timestamp().Logger()

	if err := envconfig.Process(envprefix, &env); err != nil {
		log.Fatal().Msgf("%+v", err)
	}

	log.Debug().Msgf("%+v", env)
}

func newGateway(ctx context.Context) (*http.Server, error) {
	gin.SetMode(gin.ReleaseMode)
	r := gin.New()

	r.Use(ginlogger.SetLogger(
		ginlogger.WithLogger(func(c *gin.Context, _ io.Writer, latency time.Duration) zerolog.Logger {
			return log.Logger.With().
				Timestamp().
				Int("status", c.Writer.Status()).
				Str("method", c.Request.Method).
				Str("path", c.Request.URL.Path).
				Str("ip", c.ClientIP()).
				Dur("latency", latency).
				Str("user_agent", c.Request.UserAgent()).
				Str("principal", c.Request.Header.Get("principal")).
				Logger()
		}),
		ginlogger.WithSkipPath([]string{"/healthz"}),
	))
	r.Use(gin.Recovery())

	mux := runtime.NewServeMux(
		runtime.WithMetadata(func(_ context.Context, r *http.Request) metadata.MD {
			return metadata.Pairs("principal", r.Header.Get("principal"))
		}),
	)
	if err := pb.RegisterBankingServiceHandlerFromEndpoint(ctx, mux, env.ServiceAddress, []grpc.DialOption{grpc.WithInsecure()}); err != nil {
		return nil, xerrors.Errorf("Failed to register handler: %w", err)
	}
	r.Any("/*anypath", gin.WrapH(mux))

	return &http.Server{Addr: env.GatewayAddress, Handler: r}, nil
}

func run() error {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	g, ctx := errgroup.WithContext(ctx)

	gateway, err := newGateway(ctx)
	if err != nil {
		log.Fatal().Msgf("Failed to build gateway server: %v", err)
	}
	g.Go(func() error { return gateway.ListenAndServe() })

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	defer signal.Stop(quit)

	select {
	case <-quit:
		break
	case <-ctx.Done():
		break
	}

	cancel()

	log.Info().Msg("Shutting down server...")

	ctx, timeout := context.WithTimeout(context.Background(), 5*time.Second)
	defer timeout()

	if err := gateway.Shutdown(ctx); err != nil {
		return xerrors.Errorf("failed to shutdown: %w", err)
	}

	log.Info().Msgf("Server exiting")

	return nil
}

func main() {
	if err := run(); err != nil {
		log.Fatal().Msgf("Failed to run server: %v", err)
	}
}
