import sys
from os import listdir
import matplotlib.pyplot as plt
import pandas as pd
import plotly.express as px
from sklearn.cluster import KMeans
from sklearn.cluster import DBSCAN
from sklearn.preprocessing import StandardScaler

def display_centroids_on_map(df):
    fig = px.scatter_mapbox(
            df,
            title = "Initial centroids obtained by KMeans++",
            lat='latitude',
            lon='longitude',
            zoom=10,
            height = 800,
            width = 1200
            )

    fig.update_layout(
            title_x=0.5,
            title_y=0.975,
            mapbox_style="carto-positron",  # more detailed alternative: "open-street-map"
            mapbox_zoom=10,
            mapbox_center={"lat": 40.7128, "lon": -74.0060},  # centered around NYC
            )
    
    fig.update_layout(margin={"r":0,"t":50,"l":0,"b":0})
    fig.show()



def display_clusters_on_map(df):
    fig = px.scatter_mapbox(
            df,
            title = "Result clusters",
            lat='latitude',
            lon='longitude',
            zoom=10,
            color='clusterid',
            color_continuous_scale='rainbow',
            height = 800,
            width = 1200
            )

    fig.update_layout(
            title_x=0.5,
            title_y=0.975,
            mapbox_style="carto-positron",  # more detailed alternative: "open-street-map"
            mapbox_zoom=10,
            mapbox_center={"lat": 40.7128, "lon": -74.0060},  # centered around NYC
            )

    fig.update_layout(margin={"r":0,"t":50,"l":0,"b":0})
    fig.show()


def main():
    #file = str(sys.argv[1])
    path = str(sys.argv[1])

    files = listdir(path)
    clusters = [f for f in files if f.endswith('.csv')]

    clusters_df = pd.read_csv(path + "/" + clusters[0])

    display_clusters_on_map(clusters_df)



if __name__ == '__main__':
    main()
