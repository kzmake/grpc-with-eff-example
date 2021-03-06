// Code generated by protoc-gen-go-grpc. DO NOT EDIT.

package banking

import (
	context "context"
	grpc "google.golang.org/grpc"
	codes "google.golang.org/grpc/codes"
	status "google.golang.org/grpc/status"
)

// This is a compile-time assertion to ensure that this generated file
// is compatible with the grpc package it is being compiled against.
// Requires gRPC-Go v1.32.0 or later.
const _ = grpc.SupportPackageIsVersion7

// BankingServiceClient is the client API for BankingService service.
//
// For semantics around ctx use and closing/ending streaming RPCs, please refer to https://pkg.go.dev/google.golang.org/grpc/?tab=doc#ClientConn.NewStream.
type BankingServiceClient interface {
	CreateAccount(ctx context.Context, in *CreateAccountRequest, opts ...grpc.CallOption) (*CreateAccountResponse, error)
	GetAccount(ctx context.Context, in *GetAccountRequest, opts ...grpc.CallOption) (*GetAccountResponse, error)
	DepositMoney(ctx context.Context, in *DepositMoneyRequest, opts ...grpc.CallOption) (*DepositMoneyResponse, error)
	WithdrawMoney(ctx context.Context, in *WithdrawMoneyRequest, opts ...grpc.CallOption) (*WithdrawMoneyResponse, error)
	DeleteAccount(ctx context.Context, in *DeleteAccountRequest, opts ...grpc.CallOption) (*DeleteAccountResponse, error)
}

type bankingServiceClient struct {
	cc grpc.ClientConnInterface
}

func NewBankingServiceClient(cc grpc.ClientConnInterface) BankingServiceClient {
	return &bankingServiceClient{cc}
}

func (c *bankingServiceClient) CreateAccount(ctx context.Context, in *CreateAccountRequest, opts ...grpc.CallOption) (*CreateAccountResponse, error) {
	out := new(CreateAccountResponse)
	err := c.cc.Invoke(ctx, "/banking.v1.BankingService/CreateAccount", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *bankingServiceClient) GetAccount(ctx context.Context, in *GetAccountRequest, opts ...grpc.CallOption) (*GetAccountResponse, error) {
	out := new(GetAccountResponse)
	err := c.cc.Invoke(ctx, "/banking.v1.BankingService/GetAccount", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *bankingServiceClient) DepositMoney(ctx context.Context, in *DepositMoneyRequest, opts ...grpc.CallOption) (*DepositMoneyResponse, error) {
	out := new(DepositMoneyResponse)
	err := c.cc.Invoke(ctx, "/banking.v1.BankingService/DepositMoney", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *bankingServiceClient) WithdrawMoney(ctx context.Context, in *WithdrawMoneyRequest, opts ...grpc.CallOption) (*WithdrawMoneyResponse, error) {
	out := new(WithdrawMoneyResponse)
	err := c.cc.Invoke(ctx, "/banking.v1.BankingService/WithdrawMoney", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

func (c *bankingServiceClient) DeleteAccount(ctx context.Context, in *DeleteAccountRequest, opts ...grpc.CallOption) (*DeleteAccountResponse, error) {
	out := new(DeleteAccountResponse)
	err := c.cc.Invoke(ctx, "/banking.v1.BankingService/DeleteAccount", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

// BankingServiceServer is the server API for BankingService service.
// All implementations should embed UnimplementedBankingServiceServer
// for forward compatibility
type BankingServiceServer interface {
	CreateAccount(context.Context, *CreateAccountRequest) (*CreateAccountResponse, error)
	GetAccount(context.Context, *GetAccountRequest) (*GetAccountResponse, error)
	DepositMoney(context.Context, *DepositMoneyRequest) (*DepositMoneyResponse, error)
	WithdrawMoney(context.Context, *WithdrawMoneyRequest) (*WithdrawMoneyResponse, error)
	DeleteAccount(context.Context, *DeleteAccountRequest) (*DeleteAccountResponse, error)
}

// UnimplementedBankingServiceServer should be embedded to have forward compatible implementations.
type UnimplementedBankingServiceServer struct {
}

func (UnimplementedBankingServiceServer) CreateAccount(context.Context, *CreateAccountRequest) (*CreateAccountResponse, error) {
	return nil, status.Errorf(codes.Unimplemented, "method CreateAccount not implemented")
}
func (UnimplementedBankingServiceServer) GetAccount(context.Context, *GetAccountRequest) (*GetAccountResponse, error) {
	return nil, status.Errorf(codes.Unimplemented, "method GetAccount not implemented")
}
func (UnimplementedBankingServiceServer) DepositMoney(context.Context, *DepositMoneyRequest) (*DepositMoneyResponse, error) {
	return nil, status.Errorf(codes.Unimplemented, "method DepositMoney not implemented")
}
func (UnimplementedBankingServiceServer) WithdrawMoney(context.Context, *WithdrawMoneyRequest) (*WithdrawMoneyResponse, error) {
	return nil, status.Errorf(codes.Unimplemented, "method WithdrawMoney not implemented")
}
func (UnimplementedBankingServiceServer) DeleteAccount(context.Context, *DeleteAccountRequest) (*DeleteAccountResponse, error) {
	return nil, status.Errorf(codes.Unimplemented, "method DeleteAccount not implemented")
}

// UnsafeBankingServiceServer may be embedded to opt out of forward compatibility for this service.
// Use of this interface is not recommended, as added methods to BankingServiceServer will
// result in compilation errors.
type UnsafeBankingServiceServer interface {
	mustEmbedUnimplementedBankingServiceServer()
}

func RegisterBankingServiceServer(s grpc.ServiceRegistrar, srv BankingServiceServer) {
	s.RegisterService(&BankingService_ServiceDesc, srv)
}

func _BankingService_CreateAccount_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CreateAccountRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(BankingServiceServer).CreateAccount(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/banking.v1.BankingService/CreateAccount",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(BankingServiceServer).CreateAccount(ctx, req.(*CreateAccountRequest))
	}
	return interceptor(ctx, in, info, handler)
}

func _BankingService_GetAccount_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(GetAccountRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(BankingServiceServer).GetAccount(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/banking.v1.BankingService/GetAccount",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(BankingServiceServer).GetAccount(ctx, req.(*GetAccountRequest))
	}
	return interceptor(ctx, in, info, handler)
}

func _BankingService_DepositMoney_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(DepositMoneyRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(BankingServiceServer).DepositMoney(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/banking.v1.BankingService/DepositMoney",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(BankingServiceServer).DepositMoney(ctx, req.(*DepositMoneyRequest))
	}
	return interceptor(ctx, in, info, handler)
}

func _BankingService_WithdrawMoney_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(WithdrawMoneyRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(BankingServiceServer).WithdrawMoney(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/banking.v1.BankingService/WithdrawMoney",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(BankingServiceServer).WithdrawMoney(ctx, req.(*WithdrawMoneyRequest))
	}
	return interceptor(ctx, in, info, handler)
}

func _BankingService_DeleteAccount_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(DeleteAccountRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(BankingServiceServer).DeleteAccount(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/banking.v1.BankingService/DeleteAccount",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(BankingServiceServer).DeleteAccount(ctx, req.(*DeleteAccountRequest))
	}
	return interceptor(ctx, in, info, handler)
}

// BankingService_ServiceDesc is the grpc.ServiceDesc for BankingService service.
// It's only intended for direct use with grpc.RegisterService,
// and not to be introspected or modified (even as a copy)
var BankingService_ServiceDesc = grpc.ServiceDesc{
	ServiceName: "banking.v1.BankingService",
	HandlerType: (*BankingServiceServer)(nil),
	Methods: []grpc.MethodDesc{
		{
			MethodName: "CreateAccount",
			Handler:    _BankingService_CreateAccount_Handler,
		},
		{
			MethodName: "GetAccount",
			Handler:    _BankingService_GetAccount_Handler,
		},
		{
			MethodName: "DepositMoney",
			Handler:    _BankingService_DepositMoney_Handler,
		},
		{
			MethodName: "WithdrawMoney",
			Handler:    _BankingService_WithdrawMoney_Handler,
		},
		{
			MethodName: "DeleteAccount",
			Handler:    _BankingService_DeleteAccount_Handler,
		},
	},
	Streams:  []grpc.StreamDesc{},
	Metadata: "banking/v1/banking.proto",
}
