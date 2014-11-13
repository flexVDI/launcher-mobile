//
//  Shader.fsh
//  iOSLauncher
//
//  Created by Nologin Consulting on 31/10/14.
//  Copyright (c) 2014 Flexible Software Solutions S.L. All rights reserved.
//

varying lowp vec4 colorVarying;

void main()
{
    gl_FragColor = colorVarying;
}
