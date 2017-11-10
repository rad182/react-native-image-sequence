import React, { Component } from 'react';
import ReactNative, {
  requireNativeComponent,
  ViewPropTypes,
  NativeModules,
  DeviceEventEmitter
} from 'react-native';
import { string, number, array, shape, arrayOf, func } from 'prop-types';
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';

class ImageSequence extends Component {
  componentWillMount() {
    DeviceEventEmitter.addListener('onLoad', this.onLoad);

    DeviceEventEmitter.addListener('onEnd', this.onEnd);
  }

  onLoad = event => {
    if (this.props.onLoad) {
      this.props.onLoad();
    }
  };

  onEnd = event => {
    if (this.props.onEnd) {
      this.props.onEnd();
    }
  };

  componentWillUnmount() {
    DeviceEventEmitter.removeListener('onLoad', this.onLoad);
    DeviceEventEmitter.removeListener('onEnd', this.onEnd);
  }

  play() {
    const { UIManager } = NativeModules;
    const { Commands } = NativeModules.UIManager.RCTImageSequence;
    UIManager.dispatchViewManagerCommand(
      ReactNative.findNodeHandle(this),
      Commands.play,
      []
    );
  }

  stop() {
    const { UIManager } = NativeModules;
    const { Commands } = NativeModules.UIManager.RCTImageSequence;
    UIManager.dispatchViewManagerCommand(
      ReactNative.findNodeHandle(this),
      Commands.stop,
      []
    );
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
  startFrameIndex: number,
  images: array.isRequired,
  framesPerSecond: number,
  size: shape({
    width: number,
    height: number
  }),
  onLoad: func,
  onEnd: func
};

const RCTImageSequence = requireNativeComponent('RCTImageSequence', {
  propTypes: {
    ...ViewPropTypes,
    images: arrayOf(
      shape({
        uri: string.isRequired
      })
    ).isRequired,
    framesPerSecond: number,
    size: shape({
      width: number,
      height: number
    }),
    onLoad: func,
    onEnd: func
  }
});

export default ImageSequence;
