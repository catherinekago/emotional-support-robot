// Generated by gencpp from file ultraarm_communication/SetAngles.msg
// DO NOT EDIT!


#ifndef ULTRAARM_COMMUNICATION_MESSAGE_SETANGLES_H
#define ULTRAARM_COMMUNICATION_MESSAGE_SETANGLES_H

#include <ros/service_traits.h>


#include <ultraarm_communication/SetAnglesRequest.h>
#include <ultraarm_communication/SetAnglesResponse.h>


namespace ultraarm_communication
{

struct SetAngles
{

typedef SetAnglesRequest Request;
typedef SetAnglesResponse Response;
Request request;
Response response;

typedef Request RequestType;
typedef Response ResponseType;

}; // struct SetAngles
} // namespace ultraarm_communication


namespace ros
{
namespace service_traits
{


template<>
struct MD5Sum< ::ultraarm_communication::SetAngles > {
  static const char* value()
  {
    return "3ee1ad65a48008a184aaa3297a091e0f";
  }

  static const char* value(const ::ultraarm_communication::SetAngles&) { return value(); }
};

template<>
struct DataType< ::ultraarm_communication::SetAngles > {
  static const char* value()
  {
    return "ultraarm_communication/SetAngles";
  }

  static const char* value(const ::ultraarm_communication::SetAngles&) { return value(); }
};


// service_traits::MD5Sum< ::ultraarm_communication::SetAnglesRequest> should match
// service_traits::MD5Sum< ::ultraarm_communication::SetAngles >
template<>
struct MD5Sum< ::ultraarm_communication::SetAnglesRequest>
{
  static const char* value()
  {
    return MD5Sum< ::ultraarm_communication::SetAngles >::value();
  }
  static const char* value(const ::ultraarm_communication::SetAnglesRequest&)
  {
    return value();
  }
};

// service_traits::DataType< ::ultraarm_communication::SetAnglesRequest> should match
// service_traits::DataType< ::ultraarm_communication::SetAngles >
template<>
struct DataType< ::ultraarm_communication::SetAnglesRequest>
{
  static const char* value()
  {
    return DataType< ::ultraarm_communication::SetAngles >::value();
  }
  static const char* value(const ::ultraarm_communication::SetAnglesRequest&)
  {
    return value();
  }
};

// service_traits::MD5Sum< ::ultraarm_communication::SetAnglesResponse> should match
// service_traits::MD5Sum< ::ultraarm_communication::SetAngles >
template<>
struct MD5Sum< ::ultraarm_communication::SetAnglesResponse>
{
  static const char* value()
  {
    return MD5Sum< ::ultraarm_communication::SetAngles >::value();
  }
  static const char* value(const ::ultraarm_communication::SetAnglesResponse&)
  {
    return value();
  }
};

// service_traits::DataType< ::ultraarm_communication::SetAnglesResponse> should match
// service_traits::DataType< ::ultraarm_communication::SetAngles >
template<>
struct DataType< ::ultraarm_communication::SetAnglesResponse>
{
  static const char* value()
  {
    return DataType< ::ultraarm_communication::SetAngles >::value();
  }
  static const char* value(const ::ultraarm_communication::SetAnglesResponse&)
  {
    return value();
  }
};

} // namespace service_traits
} // namespace ros

#endif // ULTRAARM_COMMUNICATION_MESSAGE_SETANGLES_H
