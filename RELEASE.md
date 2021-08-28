# Release Notes

## 1.1.0

- Add `InputHandler.touchIdlePosition` to get the position of the mouse;
- Fix Camera's children position;
- Add API to create animated sprite from a Texture;
- Replace Node API with GraphScene API;
- Create AssetsManager to manage assets loading (and force operations order in the render loop);
- Add Simulation API: simulate a move. Changes can be commit or rollback. 
  Useful to check if the player is allowed to move in some place;  
- Create FrameBuffer: this API allow you to define your own render flow with your own shaders.
