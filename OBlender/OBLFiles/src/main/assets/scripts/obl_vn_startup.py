import bpy


def _set_vietnamese_ui():
    prefs = bpy.context.preferences
    view = prefs.view

    view.language = "vi_VN"
    view.use_translate_interface = True
    view.use_translate_tooltips = True
    view.use_translate_new_dataname = True

    for scene in bpy.data.scenes:
        scene.render.fps = min(scene.render.fps, 30)


try:
    _set_vietnamese_ui()
except Exception:
    pass
