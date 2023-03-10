// Generated by gencpp from file ultraarm_communication/PumpStatusRequest.msg
// DO NOT EDIT!


#ifndef ULTRAARM_COMMUNICATION_MESSAGE_PUMPSTATUSREQUEST_H
#define ULTRAARM_COMMUNICATION_MESSAGE_PUMPSTATUSREQUEST_H


#include <string>
#include <vector>
#include <memory>

#include <ros/types.h>
#include <ros/serialization.h>
#include <ros/builtin_message_traits.h>
#include <ros/message_operations.h>


namespace ultraarm_communication
{
template <class ContainerAllocator>
struct PumpStatusRequest_
{
  typedef PumpStatusRequest_<ContainerAllocator> Type;

  PumpStatusRequest_()
    : Status(false)  {
    }
  PumpStatusRequest_(const ContainerAllocator& _alloc)
    : Status(false)  {
  (void)_alloc;
    }



   typedef uint8_t _Status_type;
  _Status_type Status;





  typedef boost::shared_ptr< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> const> ConstPtr;

}; // struct PumpStatusRequest_

typedef ::ultraarm_communication::PumpStatusRequest_<std::allocator<void> > PumpStatusRequest;

typedef boost::shared_ptr< ::ultraarm_communication::PumpStatusRequest > PumpStatusRequestPtr;
typedef boost::shared_ptr< ::ultraarm_communication::PumpStatusRequest const> PumpStatusRequestConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> >::stream(s, "", v);
return s;
}


template<typename ContainerAllocator1, typename ContainerAllocator2>
bool operator==(const ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator1> & lhs, const ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator2> & rhs)
{
  return lhs.Status == rhs.Status;
}

template<typename ContainerAllocator1, typename ContainerAllocator2>
bool operator!=(const ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator1> & lhs, const ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator2> & rhs)
{
  return !(lhs == rhs);
}


} // namespace ultraarm_communication

namespace ros
{
namespace message_traits
{





template <class ContainerAllocator>
struct IsMessage< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct HasHeader< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> const>
  : FalseType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "513e93c68ef2f26ff494445b932bb052";
  }

  static const char* value(const ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0x513e93c68ef2f26fULL;
  static const uint64_t static_value2 = 0xf494445b932bb052ULL;
};

template<class ContainerAllocator>
struct DataType< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "ultraarm_communication/PumpStatusRequest";
  }

  static const char* value(const ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> >
{
  static const char* value()
  {
    return "bool Status\n"
"\n"
;
  }

  static const char* value(const ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.Status);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER
  }; // struct PumpStatusRequest_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::ultraarm_communication::PumpStatusRequest_<ContainerAllocator>& v)
  {
    s << indent << "Status: ";
    Printer<uint8_t>::stream(s, indent + "  ", v.Status);
  }
};

} // namespace message_operations
} // namespace ros

#endif // ULTRAARM_COMMUNICATION_MESSAGE_PUMPSTATUSREQUEST_H
