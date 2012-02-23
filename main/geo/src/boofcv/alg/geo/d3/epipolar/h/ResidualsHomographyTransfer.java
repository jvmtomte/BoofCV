/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
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

package boofcv.alg.geo.d3.epipolar.h;

import boofcv.alg.geo.AssociatedPair;
import boofcv.alg.geo.d3.epipolar.EpipolarResiduals;
import georegression.geometry.GeometryMath_F64;
import georegression.struct.point.Point2D_F64;
import org.ejml.data.DenseMatrix64F;

import java.util.List;

/**
 * <p>
 * Computes the difference between the point projected by the homography and its observed location.
 * Fast to compute but less theoretically correct than others.
 * </p>
 *
 * @author Peter Abeles
 */
public class ResidualsHomographyTransfer implements EpipolarResiduals {

	// list of observations
	List<AssociatedPair> obs;

	DenseMatrix64F H = new DenseMatrix64F(3,3);
	Point2D_F64 temp = new Point2D_F64();

	@Override
	public void setObservations(List<AssociatedPair> obs) {
		this.obs = obs;
	}

	@Override
	public int getN() {
		return 9;
	}

	@Override
	public int getM() {
		return obs.size()*2;
	}

	@Override
	public void process(double[] input, double[] output) {
		H.data = input;

		
		int index = 0;
		for( int i = 0; i < obs.size(); i++ ) {
			AssociatedPair p = obs.get(i);

			GeometryMath_F64.mult(H, p.keyLoc, temp);

			output[index++] = temp.x-p.currLoc.x;
			output[index++] = temp.y-p.currLoc.y;
		}
	}
}
