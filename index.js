import React, { Component } from 'react';
import {
  View,
  requireNativeComponent,
  ViewPropTypes,
  NativeModules,
  Platform
} from 'react-native';
import resolveAssetSource
  from 'react-native/Libraries/Image/resolveAssetSource';

class ImageSequence extends Component {
  reset() {
    if (Platform.OS === 'ios') {
      const ImageSequence = NativeModules.ImageSequence;
      ImageSequence.reset();
    } else if (Platform.OS === 'android') {
      const { UIManager } = NativeModules;
      const { Commands } = NativeModules.UIManager.RCTImageSequence;
      UIManager.dispatchViewManagerCommand(
        ReactNative.findNodeHandle(this),
        Commands.reset,
        []
      );
    }
  }

  render() {
    let normalized = this.props.images.map(resolveAssetSource);

    // reorder elements if start-index is different from 0 (beginning)
    if (this.props.startFrameIndex !== 0) {
      normalized = [
        ...normalized.slice(this.props.startFrameIndex),
        ...normalized.slice(0, this.props.startFrameIndex)
      ];
    }

    return <RCTImageSequence {...this.props} images={normalized} />;
  }
}

ImageSequence.defaultProps = {
  startFrameIndex: 0,
  framesPerSecond: 24
};

ImageSequence.propTypes = {
  startFrameIndex: React.PropTypes.number,
  images: React.PropTypes.array.isRequired,
  framesPerSecond: React.PropTypes.number,
  size: React.PropTypes.shape({
    width: React.PropTypes.number,
    height: React.PropTypes.number
  })
};

const RCTImageSequence = requireNativeComponent('RCTImageSequence', {
  propTypes: {
    ...ViewPropTypes,
    images: React.PropTypes.arrayOf(
      React.PropTypes.shape({
        uri: React.PropTypes.string.isRequired
      })
    ).isRequired,
    framesPerSecond: React.PropTypes.number,
    size: React.PropTypes.shape({
      width: React.PropTypes.number,
      height: React.PropTypes.number
    })
  }
});

export default ImageSequence;
