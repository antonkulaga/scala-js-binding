
/*
 Three.js "tutorials by example"
 Author: Lee Stemkoski
 Date: July 2013 (three.js v58)

 This demo is based on the work of Jerome Etienne:
 http://learningthreejs.com/blog/2013/04/30/closing-the-gap-between-html-and-webgl/

 */

// MAIN

// standard global variables
var container, scene, camera, renderer, controls, stats;
var keyboard = new THREEx.KeyboardState();
var clock = new THREE.Clock();
// custom global variables
var rendererCSS;

init();
animate();

// FUNCTIONS 		
function init()
{
    // SCENE
    scene = new THREE.Scene();
    // CAMERA
    var SCREEN_WIDTH = window.innerWidth, SCREEN_HEIGHT = window.innerHeight;
    var VIEW_ANGLE = 45, ASPECT = SCREEN_WIDTH / SCREEN_HEIGHT, NEAR = 0.1, FAR = 20000;
    camera = new THREE.PerspectiveCamera( VIEW_ANGLE, ASPECT, NEAR, FAR);
    scene.add(camera);
    camera.position.set(0,150,400);
    camera.lookAt(scene.position);
    // RENDERER
    if ( Detector.webgl )
        renderer = new THREE.WebGLRenderer( {antialias:true} );
    else
        renderer = new THREE.CanvasRenderer();
    renderer.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
    container = document.getElementById( 'ThreeJS' );
    container.appendChild( renderer.domElement );
    // EVENTS
    THREEx.WindowResize(renderer, camera);
    THREEx.FullScreen.bindKey({ charCode : 'm'.charCodeAt(0) });
    // CONTROLS
    controls = new THREE.OrbitControls( camera, renderer.domElement );
    // STATS
    stats = new Stats();
    stats.domElement.style.position = 'absolute';
    stats.domElement.style.bottom = '0px';
    stats.domElement.style.zIndex = 100;
    container.appendChild( stats.domElement );
    // LIGHT
    var light = new THREE.PointLight(0xffffff);
    light.position.set(0,250,0);
    scene.add(light);
    // FLOOR
    var floorTexture = new THREE.ImageUtils.loadTexture( 'images/checkerboard.jpg' );
    floorTexture.wrapS = floorTexture.wrapT = THREE.RepeatWrapping;
    floorTexture.repeat.set( 10, 10 );
    var floorMaterial = new THREE.MeshBasicMaterial( { map: floorTexture, side: THREE.DoubleSide } );
    var floorGeometry = new THREE.PlaneGeometry(1000, 1000, 10, 10);
    var floor = new THREE.Mesh(floorGeometry, floorMaterial);
    floor.position.y = -0.5;
    floor.rotation.x = Math.PI / 2;
    scene.add(floor);

    ////////////
    // CUSTOM //
    ////////////

    var planeMaterial   = new THREE.MeshBasicMaterial({color: 0x000000, opacity: 0.1, side: THREE.DoubleSide });
    var planeWidth = 360;
    var planeHeight = 120;
    var planeGeometry = new THREE.PlaneGeometry( planeWidth, planeHeight );
    var planeMesh= new THREE.Mesh( planeGeometry, planeMaterial );
    planeMesh.position.y += planeHeight/2;
    // add it to the standard (WebGL) scene
    scene.add(planeMesh);

    // create a new scene to hold CSS
    cssScene = new THREE.Scene();
    // create the iframe to contain webpage
    var element	= document.createElement('iframe')
    // webpage to be loaded into iframe
    element.src	= "index.html";
    // width of iframe in pixels
    var elementWidth = 1024;
    // force iframe to have same relative dimensions as planeGeometry
    var aspectRatio = planeHeight / planeWidth;
    var elementHeight = elementWidth * aspectRatio;
    element.style.width  = elementWidth + "px";
    element.style.height = elementHeight + "px";

    // create a CSS3DObject to display element
    var cssObject = new THREE.CSS3DObject( element );
    // synchronize cssObject position/rotation with planeMesh position/rotation
    cssObject.position = planeMesh.position;
    cssObject.rotation = planeMesh.rotation;
    // resize cssObject to same size as planeMesh (plus a border)
    var percentBorder = 0.05;
    cssObject.scale.x /= (1 + percentBorder) * (elementWidth / planeWidth);
    cssObject.scale.y /= (1 + percentBorder) * (elementWidth / planeWidth);
    cssScene.add(cssObject);

    // create a renderer for CSS
    rendererCSS	= new THREE.CSS3DRenderer();
    rendererCSS.setSize( window.innerWidth, window.innerHeight );
    rendererCSS.domElement.style.position = 'absolute';
    rendererCSS.domElement.style.top	  = 0;
    rendererCSS.domElement.style.margin	  = 0;
    rendererCSS.domElement.style.padding  = 0;
    document.body.appendChild( rendererCSS.domElement );
    // when window resizes, also resize this renderer
    THREEx.WindowResize(rendererCSS, camera);

    renderer.domElement.style.position = 'absolute';
    renderer.domElement.style.top      = 0;
    // make sure original renderer appears on top of CSS renderer
    renderer.domElement.style.zIndex   = 1;
    rendererCSS.domElement.appendChild( renderer.domElement );

}

function animate()
{
    requestAnimationFrame( animate );
    render();
    update();
}

function update()
{
    if ( keyboard.pressed("z") )
    {
        // do something
    }

    controls.update();
    stats.update();
}

function render()
{
    // remember to call both renderers!
    rendererCSS.render( cssScene, camera );
    renderer.render( scene, camera );
}
