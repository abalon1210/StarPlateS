/*
 * Generated by asn1c-0.9.27 (http://lionet.info/asn1c)
 * From ASN.1 module "DSRC"
 * 	found in "J2735.asn"
 */

#ifndef	_ConfidenceSet_H_
#define	_ConfidenceSet_H_


#include <SAE_J2735/asn_application.h>

/* Including external dependencies */
#include <SAE_J2735/SpeedandHeadingandThrottleConfidence.h>
#include <SAE_J2735/TimeConfidence.h>
#include <SAE_J2735/PositionConfidenceSet.h>
#include <SAE_J2735/SteeringWheelAngleConfidence.h>
#include <SAE_J2735/ThrottleConfidence.h>
#include <SAE_J2735/constr_SEQUENCE.h>

#ifdef __cplusplus
extern "C" {
#endif

/* Forward declarations */
struct AccelSteerYawRateConfidence;

/* ConfidenceSet */
typedef struct ConfidenceSet {
	struct AccelSteerYawRateConfidence	*accelConfidence	/* OPTIONAL */;
	SpeedandHeadingandThrottleConfidence_t	*speedConfidence	/* OPTIONAL */;
	TimeConfidence_t	*timeConfidence	/* OPTIONAL */;
	PositionConfidenceSet_t	*posConfidence	/* OPTIONAL */;
	SteeringWheelAngleConfidence_t	*steerConfidence	/* OPTIONAL */;
	ThrottleConfidence_t	*throttleConfidence	/* OPTIONAL */;
	/*
	 * This type is extensible,
	 * possible extensions are below.
	 */
	
	/* Context for parsing across buffer boundaries */
	asn_struct_ctx_t _asn_ctx;
} ConfidenceSet_t;

/* Implementation */
extern asn_TYPE_descriptor_t asn_DEF_ConfidenceSet;

#ifdef __cplusplus
}
#endif

/* Referred external types */
#include <SAE_J2735/AccelSteerYawRateConfidence.h>

#endif	/* _ConfidenceSet_H_ */
#include <SAE_J2735/asn_internal.h>