#include <math.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#define _USE_MATH_DEFINES
#include <math.h>

void plane(int units, int div, std::string name)
{
	std::ofstream outfile("../../3d/" + name);

	if (!outfile.is_open())
	{
		std::cout << "Failed to open file" << std::endl;
	}
	// put code to draw cylinder in here
	float centerDistance = 0.5 * units;
	float partUnits = units / (double)div;
	for (int i = 0; i < div; i++)
	{
		for (int j = 0; j < div; j++)
		{
			outfile << "T ";
			outfile << "P " << -centerDistance + i * partUnits << " " << 0 << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << 0 << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << 0 << " " << -centerDistance + j * partUnits << " ";
			outfile << "\n";
			outfile << "T ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << 0 << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << 0 << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << 0 << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "\n";
		}
	}
	outfile.close();
}

void sphere(float r, int slices, int stacks, std::string name)
{
	std::ofstream outfile("../../3d/" + name);

	if (!outfile.is_open())
	{
		std::cout << "Failed to open file" << std::endl;
	}
	//(r * cos(ah) * sin(ab), r * sin(ah), r * cos(ah) * cos(ab)
	double ab = 0;
	double ah = -M_PI / 2;
	double secB = (M_PI * 2) / slices;
	double secH = M_PI / stacks;
	for (int i = 0; i < stacks; i++)
	{
		for (int j = 0; j < slices; j++)
		{
			outfile << "T ";
			outfile << "P " << r * cos(ah) * sin(ab) << " " << r * sin(ah) << " " << r * cos(ah) * cos(ab) << " ";
			outfile << "P " << r * cos(ah + secH) * sin(ab + secB) << " " << r * sin(ah + secH) << " " << r * cos(ah + secH) * cos(ab + secB) << " ";
			outfile << "P " << r * cos(ah + secH) * sin(ab) << " " << r * sin(ah + secH) << " " << r * cos(ah + secH) * cos(ab) << " ";
			outfile << "\n";
			if (j != 0 || j != stacks)
			{
				outfile << "T ";
				outfile << "P " << r * cos(ah) * sin(ab) << " " << r * sin(ah) << " " << r * cos(ah) * cos(ab) << " ";
				outfile << "P " << r * cos(ah) * sin(ab + secB) << " " << r * sin(ah) << " " << r * cos(ah) * cos(ab + secB) << " ";
				outfile << "P " << r * cos(ah + secH) * sin(ab + secB) << " " << r * sin(ah + secH) << " " << r * cos(ah + secH) * cos(ab + secB) << " ";
				outfile << "\n";
			}
			ab += secB;
		}
		ab = 0;
		ah += secH;
	}
	outfile.close();
}

void cone(double raio, int h, int slices, int stacks, std::string name)
{
	std::ofstream outfile("../../3d/" + name);

	if (!outfile.is_open())
	{
		std::cout << "Failed to open file" << std::endl;
	}
	double nivel1, nivel2;
	double r1 = raio, r2;
	double ab = 0;
	double secB = (M_PI * 2) / slices;

	for (double i = 0; i < stacks; i++)
	{
		nivel1 = h * (i / stacks);
		nivel2 = h * ((i + 1) / stacks);
		r2 = (raio * (h - nivel2)) / h;
		for (int j = 0; j < slices; j++)
		{
			outfile << "T ";
			outfile << "P " << r1 * sin(ab) << " " << nivel1 << " " << r1 * cos(ab) << " ";
			outfile << "P " << r1 * sin(ab + secB) << " " << nivel1 << " " << r1 * cos(ab + secB) << " ";
			outfile << "P " << r2 * sin(ab + secB) << " " << nivel2 << " " << r2 * cos(ab + secB) << " ";
			outfile << "\n";
			if (j != 1 + stacks)
			{
				outfile << "T ";
				outfile << "P " << r1 * sin(ab) << " " << nivel1 << " " << r1 * cos(ab) << " ";
				outfile << "P " << r2 * sin(ab + secB) << " " << nivel2 << " " << r2 * cos(ab + secB) << " ";
				outfile << "P " << r2 * sin(ab) << " " << nivel2 << " " << r2 * cos(ab) << " ";
				outfile << "\n";
			}
			ab += secB;
		}

		r1 = (raio * (h - nivel2)) / h;
		// std::cout << r << " "<< nivel1 << " "<< nivel2 << " "<< i<<"\n";
		ab = 0;
	}

	for (int ii = 0; ii < slices; ii++)
	{
		outfile << "T ";
		outfile << "P " << 0.0f << " " << 0.0f << " " << 0.0f << " ";
		outfile << "P " << sin(ab + secB) * raio << " " << 0.0f << " " << cos(ab + secB) * raio << " ";
		outfile << "P " << sin(ab) * raio << " " << 0.0f << " " << cos(ab) * raio << " ";
		outfile << "\n";
		ab += secB;
	}
	outfile.close();
}

void box(int units, int div, std::string name)
{
	std::ofstream outfile("../../3d/" + name);

	if (!outfile.is_open())
	{
		std::cout << "Failed to open file" << std::endl;
	}
	int i = 0, j = 0;
	// put code to draw cylinder in here
	float centerDistance = 0.5 * units;
	float partUnits = units / (double)div;
	for (int i = 0; i < div; i++)
	{
		for (int j = 0; j < div; j++)
		{
			// Base -y
			outfile << "T ";
			outfile << "P " << -centerDistance + i * partUnits << " " << -centerDistance << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << -centerDistance << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "\n";
			outfile << "T ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << -centerDistance << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "\n";
			// Teto +y
			outfile << "T ";
			outfile << "P " << -centerDistance + i * partUnits << " " << centerDistance << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << centerDistance << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << centerDistance << " " << -centerDistance + j * partUnits << " ";
			outfile << "\n";
			outfile << "T ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << centerDistance << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << centerDistance << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << centerDistance << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "\n";

			// Face -x
			outfile << "T ";
			outfile << "P " << -centerDistance << " " << -centerDistance + i * partUnits << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << -centerDistance << " " << -centerDistance + i * partUnits << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "P " << -centerDistance << " " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + j * partUnits << " ";
			outfile << "\n";
			outfile << "T ";
			outfile << "P " << -centerDistance << " " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << -centerDistance << " " << -centerDistance + i * partUnits << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "P " << -centerDistance << " " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "\n";
			// Face +x
			outfile << "T ";
			outfile << "P " << centerDistance << " " << -centerDistance + i * partUnits << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << centerDistance << " " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << centerDistance << " " << -centerDistance + i * partUnits << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "\n";
			outfile << "T ";
			outfile << "P " << centerDistance << " " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + j * partUnits << " ";
			outfile << "P " << centerDistance << " " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "P " << centerDistance << " " << -centerDistance + i * partUnits << " " << -centerDistance + (j + 1) * partUnits << " ";
			outfile << "\n";

			// Face -z
			outfile << "T ";
			outfile << "P " << -centerDistance + i * partUnits << " " << -centerDistance + j * partUnits << " " << -centerDistance << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << -centerDistance + (j + 1) * partUnits << " " << -centerDistance << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + j * partUnits << " " << -centerDistance << " ";
			outfile << "\n";
			outfile << "T ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + j * partUnits << " " << -centerDistance << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << -centerDistance + (j + 1) * partUnits << " " << -centerDistance << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + (j + 1) * partUnits << " " << -centerDistance << " ";
			outfile << "\n";
			// Teto
			outfile << "T ";
			outfile << "P " << -centerDistance + i * partUnits << " " << -centerDistance + j * partUnits << " " << centerDistance << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + j * partUnits << " " << centerDistance << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << -centerDistance + (j + 1) * partUnits << " " << centerDistance << " ";
			outfile << "\n";
			outfile << "T ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + j * partUnits << " " << centerDistance << " ";
			outfile << "P " << -centerDistance + (i + 1) * partUnits << " " << -centerDistance + (j + 1) * partUnits << " " << centerDistance << " ";
			outfile << "P " << -centerDistance + i * partUnits << " " << -centerDistance + (j + 1) * partUnits << " " << centerDistance << " ";
			outfile << "\n";
		}
	}
	outfile.close();
}

void ring(float raio1, float raio2, int slices, int stacks, std::string name)
{
	std::ofstream outfile("../../3d/" + name);

	if (!outfile.is_open())
	{
		std::cout << "Failed to open file" << std::endl;
	}

	double ab = 0;
	double secB = (M_PI * 2) / slices;
	double r1 = raio1;
	double r2 = raio1;
	for (double i = 0; i < stacks; i++)
	{
		r2 += (raio2 - raio1) / stacks;
		for (int j = 0; j < slices; j++)
		{
			outfile << "T ";
			outfile << "P " << r1 * sin(ab) << " " << 0 << " " << r1 * cos(ab) << " ";
			outfile << "P " << r1 * sin(ab + secB) << " " << 0 << " " << r1 * cos(ab + secB) << " ";
			outfile << "P " << r2 * sin(ab + secB) << " " << 0 << " " << r2 * cos(ab + secB) << " ";
			outfile << "\n";

			outfile << "T ";
			outfile << "P " << r1 * sin(ab) << " " << 0 << " " << r1 * cos(ab) << " ";
			outfile << "P " << r2 * sin(ab + secB) << " " << 0 << " " << r2 * cos(ab + secB) << " ";
			outfile << "P " << r2 * sin(ab) << " " << 0 << " " << r2 * cos(ab) << " ";
			outfile << "\n";

			outfile << "T ";
			outfile << "P " << r1 * sin(ab) << " " << 0 << " " << r1 * cos(ab) << " ";
			outfile << "P " << r2 * sin(ab + secB) << " " << 0 << " " << r2 * cos(ab + secB) << " ";
			outfile << "P " << r1 * sin(ab + secB) << " " << 0 << " " << r1 * cos(ab + secB) << " ";
			outfile << "\n";

			outfile << "T ";
			outfile << "P " << r1 * sin(ab) << " " << 0 << " " << r1 * cos(ab) << " ";
			outfile << "P " << r2 * sin(ab) << " " << 0 << " " << r2 * cos(ab) << " ";
			outfile << "P " << r2 * sin(ab + secB) << " " << 0 << " " << r2 * cos(ab + secB) << " ";
			outfile << "\n";

			ab += secB;
		}

		r1 += (raio2 - raio1) / stacks;
		// std::cout << r << " "<< nivel1 << " "<< nivel2 << " "<< i<<"\n";
		ab = 0;
	}
	outfile.close();
}

void multVectorMatrix(float *v, float *m, float *res)
{

	for (int j = 0; j < 4; ++j)
	{
		res[j] = 0;
		for (int k = 0; k < 4; ++k)
		{
			res[j] += v[k] * m[k * 4 + j];
		}
	}
}

float multVectorVector(float *v1, float *v2)
{
	float r = 0;
	for (int j = 0; j < 4; ++j)
	{
		r += v1[j] * v2[j];
	}
	return r;
}

void getBezierPoint(float u, float v, float *patchPoints, float *pos)
{
	float m[4][4] = {{-1, 3, -3, 1},
					 {3, -6, 3, 0},
					 {-3, 3, 0, 0},
					 {1, 0, 0, 0}};

	float U[4] = {u * u * u, u * u, u, 1};
	float V[4] = {v * v * v, v * v, v, 1};
	float Ps[3][16];
	for (int i = 0; i < 16; i++)
	{
		Ps[0][i] = patchPoints[i * 3];
		Ps[1][i] = patchPoints[i * 3 + 1];
		Ps[2][i] = patchPoints[i * 3 + 2];
		//printf("%f %f %f\n", Ps[0][i], Ps[1][i], Ps[2][i]);
	}
	// Compute A = U * M for each component (x, y, z)
	// Compute B = U * M * P for each component (x, y, z)
	// Compute B = U * M * P * M for each component (x, y, z)
	// Compute B = U * M * P * M * V for each component (x, y, z)
	float A[4];
	float B[4];
	for (int i = 0; i < 3; i++)
	{
		multVectorMatrix(U, m[0], A);
		multVectorMatrix(A, Ps[i], B);
		multVectorMatrix(B, m[0], A);
		pos[i] = multVectorVector(A, V);
	}
}

using namespace std;

// Parse the patch file and store the data in global variables
void parsePatchFile(string filename, int tess_level, string output_file)
{
	int numPatches, numControlPoints;

	ifstream file(filename);
	if (!file)
	{
		cerr << "Error opening file " << filename << endl;
		exit(1);
	}

	string line;
	getline(file, line);
	numPatches = stoi(line);

	int pointIndexes[numPatches][16];
	for (int i = 0; i < numPatches; i++)
	{
		getline(file, line);
		istringstream lineStream(line);
		string index;
		//printf("[PI %d] ", i);
		for (int j = 0; j < 16; j++)
		{
			lineStream >> index;
			pointIndexes[i][j] = stoi(index);
			//printf("%d ", pointIndexes[i][j]);
		}
		//printf("\n");
	}

	getline(file, line);
	numControlPoints = stoi(line);

	float controlPoints[numControlPoints][3];
	for (int i = 0; i < numControlPoints; i++)
	{
		getline(file, line);
		istringstream lineStream(line);
		string index;
		for (int j = 0; j < 3; j++)
		{
			lineStream >> index;
			controlPoints[i][j] = stof(index);
		}
	}

	int totalP = 16 * 3;
	float patchPoints[totalP], pos[3];
	std::ofstream outfile("../../3d/" + output_file);
	for (int i = 0; i < numPatches; i++)
	{
		for (int j = 0, k = 0; j < totalP; j += 3, k++)
		{
			patchPoints[j] = controlPoints[pointIndexes[i][k]][0];
			patchPoints[j + 1] = controlPoints[pointIndexes[i][k]][1];
			patchPoints[j + 2] = controlPoints[pointIndexes[i][k]][2];
			//printf("[PATCH %d] %f %f %f\n", i, patchPoints[j], patchPoints[j + 1], patchPoints[j + 2]);
		}
		for (float u = 0; u < 1; u += (float)1 / (float)tess_level)
		{
			float u_next = u + (float)1 / (float)tess_level;

			for (float v = 0; v < 1; v += (float)1 / (float)tess_level)
			{
				float v_next = v + (float)1 / (float)tess_level;

				outfile << "T ";
				getBezierPoint(u, v, patchPoints, pos);
				outfile << "P " << pos[0] << " " << pos[1] << " " << pos[2] << " ";
				getBezierPoint(u, v_next, patchPoints, pos);
				outfile << "P " << pos[0] << " " << pos[1] << " " << pos[2] << " ";
				getBezierPoint(u_next, v, patchPoints, pos);
				outfile << "P " << pos[0] << " " << pos[1] << " " << pos[2] << " ";
				outfile << "\n";
			
				outfile << "T ";
				getBezierPoint(u_next, v_next, patchPoints, pos);
				outfile << "P " << pos[0] << " " << pos[1] << " " << pos[2] << " ";
				getBezierPoint(u_next, v, patchPoints, pos);
				outfile << "P " << pos[0] << " " << pos[1] << " " << pos[2] << " ";
				getBezierPoint(u, v_next, patchPoints, pos);
				outfile << "P " << pos[0] << " " << pos[1] << " " << pos[2] << " ";
				outfile << "\n";
			}
		}
	}
}

int main(int argc, char **argv)
{
	std::string shape = argv[1];

	if (shape == "plane")
		plane(std::stoi(argv[2]), std::stoi(argv[3]), argv[4]);
	else if (shape == "box")
		box(std::stoi(argv[2]), std::stoi(argv[3]), argv[4]);
	else if (shape == "sphere")
		sphere(std::stoi(argv[2]), std::stoi(argv[3]), std::stoi(argv[4]), argv[5]);
	else if (shape == "cone")
		cone(std::stoi(argv[2]), std::stoi(argv[3]), std::stoi(argv[4]), std::stoi(argv[5]), argv[6]);
	else if (shape == "ring")
		ring(std::stoi(argv[2]), std::stoi(argv[3]), std::stoi(argv[4]), std::stoi(argv[5]), argv[6]);
	else if (shape == "patch")
		parsePatchFile(argv[2], std::stoi(argv[3]), argv[4]);
	else
	{
		std::cout << "Erro: Figura não suportada";
		return 1;
	}
	return 0;
}
