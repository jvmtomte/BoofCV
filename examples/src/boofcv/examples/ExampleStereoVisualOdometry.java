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

package boofcv.examples;

import boofcv.abst.feature.detect.extract.ConfigExtract;
import boofcv.abst.feature.disparity.StereoDisparitySparse;
import boofcv.abst.feature.tracker.PointTracker;
import boofcv.abst.sfm.AccessPointTracks3D;
import boofcv.abst.sfm.ModelAssistedTrackerCalibrated;
import boofcv.abst.sfm.StereoVisualOdometry;
import boofcv.factory.feature.disparity.FactoryStereoDisparity;
import boofcv.factory.feature.tracker.FactoryPointSequentialTracker;
import boofcv.factory.sfm.FactoryVisualOdometry;
import boofcv.io.MediaManager;
import boofcv.io.image.SimpleImageSequence;
import boofcv.io.wrapper.DefaultMediaManager;
import boofcv.misc.BoofMiscOps;
import boofcv.struct.calib.StereoParameters;
import boofcv.struct.geo.Point2D3D;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;
import georegression.struct.point.Vector3D_F64;
import georegression.struct.se.Se3_F64;

/**
 * Bare bones example showing how to estimate the camera's ego-motion using a stereo camera system. Additional
 * information on the scene can be optionally extracted from the algorithm if it implements AccessPointTracks3D.
 *
 * @author Peter Abeles
 */
public class ExampleStereoVisualOdometry {

	public static void main( String args[] ) {

		MediaManager media = DefaultMediaManager.INSTANCE;

		String directory = "../data/applet/vo/backyard/";

		// load camera description and the video sequence
		StereoParameters config = BoofMiscOps.loadXML(media.openFile(directory+"stereo.xml"));
		SimpleImageSequence<ImageUInt8> video1 = media.openVideo(directory+"left.mjpeg",ImageUInt8.class);
		SimpleImageSequence<ImageUInt8> video2 = media.openVideo(directory+"right.mjpeg",ImageUInt8.class);

		// specify how the image features are going to be tracked
		PointTracker<ImageUInt8> tracker =
				FactoryPointSequentialTracker.klt(
						600,new int[]{1,2,4,8},new ConfigExtract(3,1),3,3,2,ImageUInt8.class, ImageSInt16.class);

		// computes the depth of each point
		StereoDisparitySparse<ImageUInt8> disparity =
				FactoryStereoDisparity.regionSparseWta(0, 150, 3, 3, 30, -1, true, ImageUInt8.class);

		// declares the algorithm
		ModelAssistedTrackerCalibrated<ImageUInt8, Se3_F64,Point2D3D> assistedTracker =
				FactoryVisualOdometry.trackerP3P(tracker,1.5,400,0);
		StereoVisualOdometry<ImageUInt8> visualOdometry = FactoryVisualOdometry.stereoDepth(120, 2,
				disparity, assistedTracker, ImageUInt8.class);

		// Pass in intrinsic/extrinsic calibration.  This can be changed in the future.
		visualOdometry.setCalibration(config);

		// Process the video sequence and output the location plus number of inliers
		while( video1.hasNext() ) {
			ImageUInt8 left = video1.next();
			ImageUInt8 right = video2.next();

			if( !visualOdometry.process(left,right) ) {
				throw new RuntimeException("VO Failed!");
			}

			Se3_F64 leftToWorld = visualOdometry.getLeftToWorld();
			Vector3D_F64 T = leftToWorld.getT();

			System.out.printf("Location %8.2f %8.2f %8.2f      inliers %s\n", T.x, T.y, T.z,countInliers(visualOdometry));
		}
	}

	/**
	 * If the algorithm implements AccessPointTracks3D, then count the number of inlier features
	 * and return a string.
	 */
	public static String countInliers( StereoVisualOdometry alg ) {
		if( !(alg instanceof AccessPointTracks3D))
			return "";

		AccessPointTracks3D access = (AccessPointTracks3D)alg;

		int count = 0;
		int N = access.getAllTracks().size();
		for( int i = 0; i < N; i++ ) {
			if( access.isInlier(i) )
				count++;
		}

		return Integer.toString(count);
	}
}
