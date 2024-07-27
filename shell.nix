{ pkgs ? import <nixpkgs> {} }:
pkgs.mkShell rec {
  LD_LIBRARY_PATH = with pkgs; ''${lib.makeLibraryPath [
      alsaLib
      udev
      libGL
      xorg.libX11
      xorg.libXcursor
      vulkan-loader
      xorg.libXi
      xorg.libXinerama
      xorg.libXrandr
  ]}'';
  nativeBuildInputs = [ pkgs.pkg-config ];
  buildInputs = with pkgs; [
    clang
    llvmPackages_latest.bintools
    pkg-config
    udev
    alsaLib
    xorg.libX11
    libGL
    xorg.libXcursor
    xorg.libXinerama
    xorg.libXrandr
    xorg.libXi
    vulkan-tools
    vulkan-headers
    vulkan-loader
    vulkan-validation-layers
  ];
#  PKG_CONFIG_PATH="${pkgs.openssl.dev}/lib/pkgconfig:${pkgs.alsa-lib.dev}/lib/pkgconfig:${pkgs.udev.dev}/lib/pkgconfig";
}
