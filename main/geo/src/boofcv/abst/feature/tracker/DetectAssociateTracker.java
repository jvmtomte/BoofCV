/*
 * Copyright (c) 2011-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.abst.feature.tracker;

import boofcv.abst.feature.associate.AssociateDescription2D;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.struct.FastQueue;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.image.ImageSingleBand;
import georegression.struct.point.Point2D_F64;


/**
 * <p>
 * Tracker for image features which are first detected and then associated using the extracted
 * feature description.  For this tracker to work well the feature descriptor must be very strong
 * and result in the correct association without any model of the model being fit.
 * </p>
 *
 * @author Peter Abeles
 */
public class DetectAssociateTracker<I extends ImageSingleBand, TD extends TupleDesc,Aux>
		extends DetectAssociateBase<I, TD,Aux> {

	// Feature detector and describer
	protected DetectDescribePoint<I, TD> detDesc;

	/**
	 * Configures tracker
	 *
	 * @param detDesc Feature detector and descriptor
	 * @param associate Association
	 * @param updateDescription If true then the feature description will be updated after each image.
	 *                          Typically this should be false.
	 */
	public DetectAssociateTracker( final DetectDescribePoint<I, TD> detDesc ,
								   final AssociateDescription2D<TD> associate ,
								   final boolean updateDescription ) {
		super(associate,updateDescription,detDesc.getDescriptorType());
		this.detDesc = detDesc;
	}

	@Override
	protected void detectFeatures(I input, FastQueue<Point2D_F64> locDst, FastQueue<TD> featDst) {
		detDesc.detect(input);

		int N = detDesc.getNumberOfFeatures();
		for( int i = 0; i < N; i++ ) {
			locDst.add( detDesc.getLocation(i) );
			featDst.add( detDesc.getDescriptor(i) );
		}
	}

	@Override
	protected TD createDescription() {
		return detDesc.createDescription();
	}

	@Override
	public Aux getAuxiliary(PointTrack track) {
		return null;
	}

	@Override
	public PointTrack addTrack(double x, double y, Object auxiliary) {
		return null;
	}
}
