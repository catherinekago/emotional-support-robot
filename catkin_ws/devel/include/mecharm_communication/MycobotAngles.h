// Generated by gencpp from file mecharm_communication/MycobotAngles.msg
// DO NOT EDIT!


#ifndef MECHARM_COMMUNICATION_MESSAGE_MYCOBOTANGLES_H
#define MECHARM_COMMUNICATION_MESSAGE_MYCOBOTANGLES_H


#include <string>
#include <vector>
#include <memory>

#include <ros/types.h>
#include <ros/serialization.h>
#include <ros/builtin_message_traits.h>
#include <ros/message_operations.h>


namespace mecharm_communication
{
template <class ContainerAllocator>
struct MycobotAngles_
{
  typedef MycobotAngles_<ContainerAllocator> Type;

  MycobotAngles_()
    : joint_1(0.0)
    , joint_2(0.0)
    , joint_3(0.0)
    , joint_4(0.0)
    , joint_5(0.0)
    , joint_6(0.0)  {
    }
  MycobotAngles_(const ContainerAllocator& _alloc)
    : joint_1(0.0)
    , joint_2(0.0)
    , joint_3(0.0)
    , joint_4(0.0)
    , joint_5(0.0)
    , joint_6(0.0)  {
  (void)_alloc;
    }



   typedef float _joint_1_type;
  _joint_1_type joint_1;

   typedef float _joint_2_type;
  _joint_2_type joint_2;

   typedef float _joint_3_type;
  _joint_3_type joint_3;

   typedef float _joint_4_type;
  _joint_4_type joint_4;

   typedef float _joint_5_type;
  _joint_5_type joint_5;

   typedef float _joint_6_type;
  _joint_6_type joint_6;





  typedef boost::shared_ptr< ::mecharm_communication::MycobotAngles_<ContainerAllocator> > Ptr;
  typedef boost::shared_ptr< ::mecharm_communication::MycobotAngles_<ContainerAllocator> const> ConstPtr;

}; // struct MycobotAngles_

typedef ::mecharm_communication::MycobotAngles_<std::allocator<void> > MycobotAngles;

typedef boost::shared_ptr< ::mecharm_communication::MycobotAngles > MycobotAnglesPtr;
typedef boost::shared_ptr< ::mecharm_communication::MycobotAngles const> MycobotAnglesConstPtr;

// constants requiring out of line definition



template<typename ContainerAllocator>
std::ostream& operator<<(std::ostream& s, const ::mecharm_communication::MycobotAngles_<ContainerAllocator> & v)
{
ros::message_operations::Printer< ::mecharm_communication::MycobotAngles_<ContainerAllocator> >::stream(s, "", v);
return s;
}


template<typename ContainerAllocator1, typename ContainerAllocator2>
bool operator==(const ::mecharm_communication::MycobotAngles_<ContainerAllocator1> & lhs, const ::mecharm_communication::MycobotAngles_<ContainerAllocator2> & rhs)
{
  return lhs.joint_1 == rhs.joint_1 &&
    lhs.joint_2 == rhs.joint_2 &&
    lhs.joint_3 == rhs.joint_3 &&
    lhs.joint_4 == rhs.joint_4 &&
    lhs.joint_5 == rhs.joint_5 &&
    lhs.joint_6 == rhs.joint_6;
}

template<typename ContainerAllocator1, typename ContainerAllocator2>
bool operator!=(const ::mecharm_communication::MycobotAngles_<ContainerAllocator1> & lhs, const ::mecharm_communication::MycobotAngles_<ContainerAllocator2> & rhs)
{
  return !(lhs == rhs);
}


} // namespace mecharm_communication

namespace ros
{
namespace message_traits
{





template <class ContainerAllocator>
struct IsMessage< ::mecharm_communication::MycobotAngles_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsMessage< ::mecharm_communication::MycobotAngles_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::mecharm_communication::MycobotAngles_<ContainerAllocator> >
  : TrueType
  { };

template <class ContainerAllocator>
struct IsFixedSize< ::mecharm_communication::MycobotAngles_<ContainerAllocator> const>
  : TrueType
  { };

template <class ContainerAllocator>
struct HasHeader< ::mecharm_communication::MycobotAngles_<ContainerAllocator> >
  : FalseType
  { };

template <class ContainerAllocator>
struct HasHeader< ::mecharm_communication::MycobotAngles_<ContainerAllocator> const>
  : FalseType
  { };


template<class ContainerAllocator>
struct MD5Sum< ::mecharm_communication::MycobotAngles_<ContainerAllocator> >
{
  static const char* value()
  {
    return "8ce9dd71b812ac669ff127f95e5ce8ab";
  }

  static const char* value(const ::mecharm_communication::MycobotAngles_<ContainerAllocator>&) { return value(); }
  static const uint64_t static_value1 = 0x8ce9dd71b812ac66ULL;
  static const uint64_t static_value2 = 0x9ff127f95e5ce8abULL;
};

template<class ContainerAllocator>
struct DataType< ::mecharm_communication::MycobotAngles_<ContainerAllocator> >
{
  static const char* value()
  {
    return "mecharm_communication/MycobotAngles";
  }

  static const char* value(const ::mecharm_communication::MycobotAngles_<ContainerAllocator>&) { return value(); }
};

template<class ContainerAllocator>
struct Definition< ::mecharm_communication::MycobotAngles_<ContainerAllocator> >
{
  static const char* value()
  {
    return "float32 joint_1\n"
"float32 joint_2\n"
"float32 joint_3\n"
"float32 joint_4\n"
"float32 joint_5\n"
"float32 joint_6\n"
;
  }

  static const char* value(const ::mecharm_communication::MycobotAngles_<ContainerAllocator>&) { return value(); }
};

} // namespace message_traits
} // namespace ros

namespace ros
{
namespace serialization
{

  template<class ContainerAllocator> struct Serializer< ::mecharm_communication::MycobotAngles_<ContainerAllocator> >
  {
    template<typename Stream, typename T> inline static void allInOne(Stream& stream, T m)
    {
      stream.next(m.joint_1);
      stream.next(m.joint_2);
      stream.next(m.joint_3);
      stream.next(m.joint_4);
      stream.next(m.joint_5);
      stream.next(m.joint_6);
    }

    ROS_DECLARE_ALLINONE_SERIALIZER
  }; // struct MycobotAngles_

} // namespace serialization
} // namespace ros

namespace ros
{
namespace message_operations
{

template<class ContainerAllocator>
struct Printer< ::mecharm_communication::MycobotAngles_<ContainerAllocator> >
{
  template<typename Stream> static void stream(Stream& s, const std::string& indent, const ::mecharm_communication::MycobotAngles_<ContainerAllocator>& v)
  {
    s << indent << "joint_1: ";
    Printer<float>::stream(s, indent + "  ", v.joint_1);
    s << indent << "joint_2: ";
    Printer<float>::stream(s, indent + "  ", v.joint_2);
    s << indent << "joint_3: ";
    Printer<float>::stream(s, indent + "  ", v.joint_3);
    s << indent << "joint_4: ";
    Printer<float>::stream(s, indent + "  ", v.joint_4);
    s << indent << "joint_5: ";
    Printer<float>::stream(s, indent + "  ", v.joint_5);
    s << indent << "joint_6: ";
    Printer<float>::stream(s, indent + "  ", v.joint_6);
  }
};

} // namespace message_operations
} // namespace ros

#endif // MECHARM_COMMUNICATION_MESSAGE_MYCOBOTANGLES_H
