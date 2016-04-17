package iso.io.iso.threading;

import iso.io.iso.algorithms.mesh.MeshCloud;

public interface MeshWorkerCallback{
  void meshWorkerCompleted(MeshCloud data);
}
