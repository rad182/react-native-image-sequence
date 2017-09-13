import React, { Component } from 'react';
import ReactNative, {
  requireNativeComponent,
  ViewPropTypes,
  NativeModules
} from 'react-native';
import { DeviceEventEmitter } from 'react-native';
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
  startFrameIndex: React.PropTypes.number,
  images: React.PropTypes.array.isRequired,
  framesPerSecond: React.PropTypes.number,
  size: React.PropTypes.shape({
    width: React.PropTypes.number,
    height: React.PropTypes.number
  }),
  onLoad: React.PropTypes.func,
  onEnd: React.PropTypes.func
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
    }),
    onLoad: React.PropTypes.func,
    onEnd: React.PropTypes.func
  }
});

export default ImageSequence;
