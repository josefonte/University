#ifdef __APPLE__
#include <GLUT/glut.h>
#else
#include <GL/glew.h>
#include <GL/glut.h>
#endif

#define _USE_MATH_DEFINES
#include <iostream>
#include <string>
#include <fstream>
#include <sstream>
#include <math.h>
#include "rapidxml.hpp"
#include "rapidxml_print.hpp"
#include "rapidxml_utils.hpp"
#include <vector>

static float ab;
static float ah;
static float camaraR;

float positionX, positionY, positionZ;
float lookAtX, lookAtY, lookAtZ;
float upX, upY, upZ;
float fov, near, far;
int width, height;
std::string xml, axe;
std::vector<float> FigPoints;
GLuint buffers[1];
std::vector<int> startIndex;
std::vector<int> numPoints;
int figCounter = 0;

void changeSize(int w, int h)
{

	// Prevent a divide by zero, when window is too short
	// (you cant make a window with zero width).
	if (h == 0)
		h = 1;

	// compute window's aspect ratio
	float ratio = w * 1.0 / h;

	// Set the projection matrix as current
	glMatrixMode(GL_PROJECTION);
	// Load Identity Matrix
	glLoadIdentity();

	// Set the viewport to be the entire window
	glViewport(0, 0, w, h);

	// Set perspective
	float aspectRatio = (float)width / (float)height;
	gluPerspective(fov, aspectRatio, near, far);

	// return to the model view matrix mode
	glMatrixMode(GL_MODELVIEW);
}

void axes(std::string str)
{
	if (str == "y")
	{
		glPushMatrix();
		glBegin(GL_LINES);
		glColor3f(1.0f, 0.0f, 0.0f);
		glVertex3f(-1000.0f, 0.0f, 0.0f);
		glVertex3f(1000.0f, 0.0f, 0.0f);
		glColor3f(0.0f, 1.0f, 0.0f);
		glVertex3f(0.0f, -1000.0f, 0.0f);
		glVertex3f(0.0f, 1000.0f, 0.0f);
		glColor3f(0.0f, 0.0f, 1.0f);
		glVertex3f(0.0f, 0.0f, -1000.0f);
		glVertex3f(0.0f, 0.0f, 1000.0f);
		glColor3f(1.0f, 1.0f, 1.0f);
		glEnd();
		glPopMatrix();
	}
}

void drawModel(int index)
{
	glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
	glVertexPointer(3, GL_FLOAT, 0, 0);
	
	int inicio, fim;
    if (index == 0) {
        inicio = 0;
        fim = numPoints[index];
    }
    else {
        inicio = numPoints[index-1];
        fim = numPoints[index];
    }
    int vertices = (fim - inicio);
	glDrawArrays(GL_TRIANGLES, inicio, vertices);
}

void ringModel(std::string fileName, int n, int r1, int r2)
{
	float x, angle;
	srand(1);
	for (int i = 0; i < n; i++)
	{

		x = rand() % (r2 - r1 + 1) + r1;
		angle = rand() % (360 + 1);

		glPushMatrix();
		glRotatef(angle, 0, 1, 0);
		glTranslatef(x, 0, 0);
		drawModel(figCounter);
		glPopMatrix();
	}
	figCounter++;
}

void multMatrixVector(float *m, float *v, float *res)
{

	for (int j = 0; j < 4; ++j)
	{
		res[j] = 0;
		for (int k = 0; k < 4; ++k)
		{
			res[j] += v[k] * m[j * 4 + k];
		}
	}
}

void cross(float *a, float *b, float *res)
{

	res[0] = a[1] * b[2] - a[2] * b[1];
	res[1] = a[2] * b[0] - a[0] * b[2];
	res[2] = a[0] * b[1] - a[1] * b[0];
}
void buildRotMatrix(float *x, float *y, float *z, float *m)
{

	m[0] = x[0];
	m[1] = x[1];
	m[2] = x[2];
	m[3] = 0;
	m[4] = y[0];
	m[5] = y[1];
	m[6] = y[2];
	m[7] = 0;
	m[8] = z[0];
	m[9] = z[1];
	m[10] = z[2];
	m[11] = 0;
	m[12] = 0;
	m[13] = 0;
	m[14] = 0;
	m[15] = 1;
}
float length(float *v)
{

	float res = sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	return res;
}

void getCatmullRomPoint(float t,
						float *p0, float *p1, float *p2, float *p3,
						float *pos, float *deriv)
{
	// Catmull-Rom basis matrix
	float m[4][4] = {{-0.5f, 1.5f, -1.5f, 0.5f},
					 {1.0f, -2.5f, 2.0f, -0.5f},
					 {-0.5f, 0.0f, 0.5f, 0.0f},
					 {0.0f, 1.0f, 0.0f, 0.0f}};

	float T[4] = {t * t * t, t * t, t, 1};

	// Compute A = M * P for each component (x, y, z)
	float A[3][4];
	for (int i = 0; i < 3; i++)
	{
		float P[4] = {p0[i], p1[i], p2[i], p3[i]};
		multMatrixVector(&m[0][0], P, A[i]);
	}

	// Compute pos = T * A for each component (x, y, z)
	for (int i = 0; i < 3; i++)
	{
		pos[i] = T[0] * A[i][0] + T[1] * A[i][1] + T[2] * A[i][2] + T[3] * A[i][3];
	}

	// Compute T' vector based on input t
	float Tp[4] = {3 * t * t, 2 * t, 1, 0};

	// Compute deriv = T' * A for each component (x, y, z)
	for (int i = 0; i < 3; i++)
	{
		deriv[i] = Tp[0] * A[i][0] + Tp[1] * A[i][1] + Tp[2] * A[i][2] + Tp[3] * A[i][3];
	}
}

void getGlobalCatmullRomPoint(float gt, float *pos, float *deriv, int point_count, std::vector<float> p)
{

	float t = gt * point_count; // this is the real global t
	int index = floor(t);		// which segment
	t = t - index;				// where within  the segment

	// indices store the points
	int indices[4];
	indices[0] = (index + point_count - 1) % point_count;
	indices[1] = (indices[0] + 1) % point_count;
	indices[2] = (indices[1] + 1) % point_count;
	indices[3] = (indices[2] + 1) % point_count;

	getCatmullRomPoint(t, &p[indices[0] * 3], &p[indices[1] * 3], &p[indices[2] * 3], &p[indices[3] * 3], pos, deriv);
}

void animateTranslate(std::vector<float> points, float duration, bool isAligned)
{
	static float lastY[3] = {0, 1.0f, 0};
	float elapsedTime = glutGet(GLUT_ELAPSED_TIME) / 1000.0f;
	float time_loop = fmod(elapsedTime, duration);
	float gt = time_loop / duration;
	int num_points = points.size() / 3;
	float pos[3], deriv[3];
	glBegin(GL_LINE_LOOP);
	float j = 0;
	for (int i = 0; i < 100; i++)
	{
		j += 0.01f;
		getGlobalCatmullRomPoint(j, pos, deriv, num_points, points);
		glVertex3f(pos[0], pos[1], pos[2]);
	}
	glEnd();
	getGlobalCatmullRomPoint(gt, pos, deriv, num_points, points);
	glTranslatef(pos[0], pos[1], pos[2]);
	if (isAligned)
	{
		float X[3], Z[3];
		float moduloDeriv = length(deriv);
		X[0] = deriv[0] / moduloDeriv;
		X[1] = deriv[1] / moduloDeriv;
		X[2] = deriv[2] / moduloDeriv;

		cross(X, lastY, Z);
		cross(Z, X, lastY);
		float m[16];
		buildRotMatrix(X, lastY, Z, m);
		glMultMatrixf(m);
	}
}

void rotateTime(float time, float x, float y, float z)
{
	float tempoAtual = glutGet(GLUT_ELAPSED_TIME) / 1000.0f;
	float angle = 360.0f * fdiv(tempoAtual, time);
	glRotatef(angle, x, y, z);
}

int counter = 0;

void storePoints(std::string pathfile, int figIndex)
{
	double x1, x2, x3, y1, y2, y3, z1, z2, z3;

	std::ifstream infile(pathfile);

	// Check if the file is open
	if (!infile.is_open())
	{
		std::cout << "Failed to open file" << std::endl;
	}

	std::string line;
	
	while (std::getline(infile, line))
	{
		// Create a stringstream to read the line
		std::stringstream ss(line);

		// Extract the point coordinates from the line
		std::string type;
		ss >> type;

		if (type == "T")
		{
			ss >> type >> x1 >> y1 >> z1;
			ss >> type >> x2 >> y2 >> z2;
			ss >> type >> x3 >> y3 >> z3;
			FigPoints.push_back(x1);
			FigPoints.push_back(y1);
			FigPoints.push_back(z1);
			FigPoints.push_back(x2);
			FigPoints.push_back(y2);
			FigPoints.push_back(z2);
			FigPoints.push_back(x3);
			FigPoints.push_back(y3);
			FigPoints.push_back(z3);
			counter += 3;
		}
	}
	numPoints.push_back(counter);
	// Close the file
	infile.close();
}

void storeModels(rapidxml::xml_node<> *groupNode)
{
	for (rapidxml::xml_node<> *ingroupNode = groupNode->first_node(); ingroupNode; ingroupNode = ingroupNode->next_sibling())
	{
		if (std::string(ingroupNode->name()) == "models")
		{
			for (rapidxml::xml_node<> *model_node = ingroupNode->first_node(); model_node; model_node = model_node->next_sibling())
			{
				if (std::string(model_node->name()) == "model")
				{
					std::string fileName = model_node->first_attribute("file")->value();
					storePoints("../../3d/" + fileName, figCounter++);
				}
				else if (std::string(model_node->name()) == "ring")
				{
					std::string fileName = model_node->first_attribute("file")->value();
					storePoints("../../3d/" + fileName, figCounter++);
				}
			}
		}
		else if (std::string(ingroupNode->name()) == "group")
		{
			// std::cout << "ler group\n";
			storeModels(ingroupNode);
		}
	}
}

void parseGroups(rapidxml::xml_node<> *groupNode)
{
	glPushMatrix();
	for (rapidxml::xml_node<> *ingroupNode = groupNode->first_node(); ingroupNode; ingroupNode = ingroupNode->next_sibling())
	{
		if (std::string(ingroupNode->name()) == "transform")
		{
			for (rapidxml::xml_node<> *transformNode = ingroupNode->first_node(); transformNode; transformNode = transformNode->next_sibling())
			{
				if (std::string(transformNode->name()) == "translate")
				{
					if (transformNode->first_attribute("x"))
					{
						float x = std::stod(transformNode->first_attribute("x")->value());
						float y = std::stod(transformNode->first_attribute("y")->value());
						float z = std::stod(transformNode->first_attribute("z")->value());
						glTranslatef(x, y, z);
					}
					else
					{
						float time = std::stof(transformNode->first_attribute("time")->value());
						bool align = (std::string(transformNode->first_attribute("align")->value())) == "true" ? true : false;

						std::vector<float> Ps;
						for (rapidxml::xml_node<> *pointNode = transformNode->first_node(); pointNode; pointNode = pointNode->next_sibling())
						{
							if (std::string(pointNode->name()) == "point")
							{
								float x = std::stod(pointNode->first_attribute("x")->value());
								Ps.push_back(x);
								float y = std::stod(pointNode->first_attribute("y")->value());
								Ps.push_back(y);
								float z = std::stod(pointNode->first_attribute("z")->value());
								Ps.push_back(z);
							}
						}
						animateTranslate(Ps, time, align);
					}
				}
				else if (std::string(transformNode->name()) == "rotate")
				{
					if (transformNode->first_attribute("angle"))
					{
						float angle = std::stod(transformNode->first_attribute("angle")->value());
						float x = std::stod(transformNode->first_attribute("x")->value());
						float y = std::stod(transformNode->first_attribute("y")->value());
						float z = std::stod(transformNode->first_attribute("z")->value());
						glRotatef(angle, x, y, z);
					}
					else
					{
						float time = std::stod(transformNode->first_attribute("time")->value());
						float x = std::stod(transformNode->first_attribute("x")->value());
						float y = std::stod(transformNode->first_attribute("y")->value());
						float z = std::stod(transformNode->first_attribute("z")->value());
						rotateTime(time, x, y, z);
					}
				}
				else if (std::string(transformNode->name()) == "scale")
				{
					float x = std::stod(transformNode->first_attribute("x")->value());
					float y = std::stod(transformNode->first_attribute("y")->value());
					float z = std::stod(transformNode->first_attribute("z")->value());
					glScalef(x, y, z);
				}
				else if (std::string(transformNode->name()) == "color")
				{
					float r = std::stod(transformNode->first_attribute("r")->value());
					float g = std::stod(transformNode->first_attribute("g")->value());
					float b = std::stod(transformNode->first_attribute("b")->value());
					glColor3f(r, g, b);
				}
			}
		}
		else if (std::string(ingroupNode->name()) == "models")
		{
			for (rapidxml::xml_node<> *model_node = ingroupNode->first_node(); model_node; model_node = model_node->next_sibling())
			{
				if (std::string(model_node->name()) == "model")
				{
					std::string fileName = model_node->first_attribute("file")->value();
					drawModel(figCounter++);
				}
				else if (std::string(model_node->name()) == "ring")
				{
					std::string fileName = model_node->first_attribute("file")->value();
					int n = std::stod(model_node->first_attribute("n")->value());
					int r1 = std::stod(model_node->first_attribute("r1")->value());
					int r2 = std::stod(model_node->first_attribute("r2")->value());
					ringModel(fileName, n, r1, r2);
				}
			}
		}
		else if (std::string(ingroupNode->name()) == "group")
		{
			// std::cout << "ler group\n";

			parseGroups(ingroupNode);
		}
	}
	glPopMatrix();
}
rapidxml::xml_node<> *groupNode;

int parseXML(rapidxml::xml_node<> *worldNode)
{
	rapidxml::xml_node<> *windowNode = worldNode->first_node("window");

	width = std::stoi(windowNode->first_attribute("width")->value());
	height = std::stoi(windowNode->first_attribute("height")->value());

	std::cout << "Window width: " << width << std::endl;
	std::cout << "Window height: " << height << std::endl;

	rapidxml::xml_node<> *cameraNode = worldNode->first_node("camera");

	rapidxml::xml_node<> *positionNode = cameraNode->first_node("position");
	rapidxml::xml_node<> *lookAtNode = cameraNode->first_node("lookAt");
	rapidxml::xml_node<> *upNode = cameraNode->first_node("up");
	rapidxml::xml_node<> *projectionNode = cameraNode->first_node("projection");

	positionX = std::stod(positionNode->first_attribute("x")->value());
	positionY = std::stod(positionNode->first_attribute("y")->value());
	positionZ = std::stod(positionNode->first_attribute("z")->value());

	lookAtX = std::stod(lookAtNode->first_attribute("x")->value());
	lookAtY = std::stod(lookAtNode->first_attribute("y")->value());
	lookAtZ = std::stod(lookAtNode->first_attribute("z")->value());

	upX = std::stod(upNode->first_attribute("x")->value());
	upY = std::stod(upNode->first_attribute("y")->value());
	upZ = std::stod(upNode->first_attribute("z")->value());

	fov = std::stod(projectionNode->first_attribute("fov")->value());
	near = std::stod(projectionNode->first_attribute("near")->value());
	far = std::stod(projectionNode->first_attribute("far")->value());

	groupNode = worldNode->first_node("group");

	storeModels(groupNode);
	glEnableClientState(GL_VERTEX_ARRAY);
	glGenBuffers(1, buffers);
	glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
	glBufferData(GL_ARRAY_BUFFER, FigPoints.size() * sizeof(float), FigPoints.data(), GL_STATIC_DRAW);

	return 0;
}

void renderScene(void)
{

	// clear buffers
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	// set the camera
	glLoadIdentity();
	gluLookAt(positionX, positionY, positionZ,
			  lookAtX, lookAtY, lookAtZ,
			  upX, upY, upZ);

	glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

	figCounter = 0;
	axes(axe);
	parseGroups(groupNode);
	
	// End of frame
	glutSwapBuffers();
}

void processKeys(unsigned char c, int xx, int yy)
{
	switch (c)
	{
	case 'q':
		camaraR += 1;
		break;
	case 'e':
		camaraR -= 1;
		break;
	}

	glutPostRedisplay();
	// put code to process regular keys in here
}

void processSpecialKeys(int key, int xx, int yy)
{
	switch (key)
	{
	case GLUT_KEY_LEFT:
		ab += 0.1;
		break;
	case GLUT_KEY_RIGHT:
		ab -= 0.1;
		break;
	case GLUT_KEY_UP:
		if (ah <= 1.4)
		{
			ah += 0.1;
		}
		break;
	case GLUT_KEY_DOWN:
		if (ah >= -1.4)
		{
			ah -= 0.1;
		}
		break;
	case GLUT_KEY_INSERT:
		glDisable(GL_CULL_FACE);
		break;
	case GLUT_KEY_F1:
		glEnable(GL_CULL_FACE);
		break;
	}
	glutPostRedisplay();
	// put code to process special keys in here
}

int main(int argc, char **argv)
{
	std::cout << "Nome de ficheiro XLM : ";
	std::getline(std::cin, xml);

	std::ifstream infile("../../xml/" + xml);
	rapidxml::file<> xmlFile(infile);
	rapidxml::xml_document<> doc;
	doc.parse<0>(xmlFile.data());
	rapidxml::xml_node<> *worldNode = doc.first_node("world");

	std::cout << "Eixos ligados (y/n): ";
	std::getline(std::cin, axe);

	// init GLUT and the window
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DEPTH | GLUT_DOUBLE | GLUT_RGBA);
	glutInitWindowPosition(100, 100);
	glutInitWindowSize(800, 800);
	glutCreateWindow("CG@DI-UM");

	glewInit();
	parseXML(worldNode);

	// Required callback registry
	glutDisplayFunc(renderScene);
	glutIdleFunc(renderScene);
	glutReshapeFunc(changeSize);

	// Callback registration for keyboard processing
	glutKeyboardFunc(processKeys);
	glutSpecialFunc(processSpecialKeys);

	//  OpenGL settings
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_CULL_FACE);

	// enter GLUT's main cycle
	glutMainLoop();

	return 1;
}
