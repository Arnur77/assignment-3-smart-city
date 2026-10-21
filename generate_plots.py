from pathlib import Path
import csv
import matplotlib.pyplot as plt

ROOT = Path(__file__).parent
TABLES = ROOT / "results" / "tables"
PLOTS = ROOT / "results" / "plots"
PLOTS.mkdir(parents=True, exist_ok=True)

def read_csv(path):
    with path.open(newline="", encoding="utf-8") as f:
        return list(csv.DictReader(f))

mst = read_csv(TABLES / "mst_results.csv")
dijkstra = read_csv(TABLES / "dijkstra_results.csv")

def plot_series(rows, xkey, xlabel, path, title):
    plt.figure(figsize=(8, 5))
    for name in sorted({r['algorithm'] for r in rows}):
        data = [r for r in rows if r['algorithm'] == name]
        data.sort(key=lambda r: int(r[xkey]))
        plt.plot([int(r[xkey]) for r in data], [int(r['average_time_ns']) / 1e6 for r in data], marker='o', label=name)
    plt.xlabel(xlabel)
    plt.ylabel('Average execution time (ms)')
    plt.title(title)
    plt.grid(True, alpha=0.25)
    plt.legend()
    plt.tight_layout()
    plt.savefig(path, dpi=160)
    plt.close()

plot_series(mst + dijkstra, 'vertices', 'Number of vertices', PLOTS / 'execution_time_vs_vertices.png', 'Execution Time vs Number of Vertices')
plot_series(mst + dijkstra, 'edges', 'Number of edges', PLOTS / 'execution_time_vs_edges.png', 'Execution Time vs Number of Edges')
print('Plots generated in', PLOTS)
