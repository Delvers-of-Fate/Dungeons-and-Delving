#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D u_texture1;
uniform sampler2D u_texture2;
uniform sampler2D u_texture3;

uniform float u_brightness;

vec4 blur9(sampler2D image, vec2 uv, vec2 resolution, vec2 direction);

void main()
{    
    vec4 color = texture2D(u_texture, v_texCoords);

    // combine blurs to do a high pass glow
    vec4 blur_color_wide = texture2D(u_texture1, v_texCoords);
    blur_color_wide *= blur_color_wide;

    vec4 blur_color_mid = texture2D(u_texture2, v_texCoords);
    blur_color_mid *= blur_color_mid;

    vec4 blur_color_narrow = texture2D(u_texture3, v_texCoords);
    blur_color_narrow *= blur_color_narrow;

    // bias the blurs to get a nice shape
    blur_color_wide.rgb *= 1.0;
    blur_color_mid.rgb *= 0.1;
    blur_color_narrow.rgb *= 0.05;

    color += blur_color_narrow + blur_color_mid + blur_color_wide;
    color = clamp(color, 0.0, 1.0);

    color.rgb = -1.0 / 2.0 * (cos(3.14159 * color.rgb / 1.0 ) - 1.0) + 0.0;
    if(color.r < 0.08) {
    	color.r = 0.0;
	}
	if(color.g < 0.08) {
    	color.g = 0.0;
	}
	if(color.b < 0.08) {
    	color.b = 0.0;
	}
	color.a = 1.0;

    gl_FragColor = color;
}