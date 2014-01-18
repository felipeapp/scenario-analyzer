<?xml version="1.0" encoding="utf-8"?>
<project path="" name="Scrtips UFRN" author="Ricardo Wendell" version="1.0" copyright="$projectName&#xD;&#xA;Copyright(c) 2006, $author.&#xD;&#xA;&#xD;&#xA;This code is licensed under BSD license. Use it as you wish, &#xD;&#xA;but keep this copyright intact." output="$project\build" source="False" source-dir="$output\source" minify="True" min-dir="$output\build" doc="False" doc-dir="$output\docs" master="true" master-file="$output\yui-ext.js" zip="true" zip-file="$output\yuo-ext.$version.zip">
  <directory name="" />
  <file name="yui\utilities.js" path="yui" />
  <file name="yui-ext\yui-ext.js" path="yui-ext" />
  <file name="util\include.js" path="util" />
  <target name="UFRN Scripts" file="$output\ufrn.js" debug="True" shorthand="False" shorthand-list="YAHOO.util.Dom.setStyle&#xD;&#xA;YAHOO.util.Dom.getStyle&#xD;&#xA;YAHOO.util.Dom.getRegion&#xD;&#xA;YAHOO.util.Dom.getViewportHeight&#xD;&#xA;YAHOO.util.Dom.getViewportWidth&#xD;&#xA;YAHOO.util.Dom.get&#xD;&#xA;YAHOO.util.Dom.getXY&#xD;&#xA;YAHOO.util.Dom.setXY&#xD;&#xA;YAHOO.util.CustomEvent&#xD;&#xA;YAHOO.util.Event.addListener&#xD;&#xA;YAHOO.util.Event.getEvent&#xD;&#xA;YAHOO.util.Event.getTarget&#xD;&#xA;YAHOO.util.Event.preventDefault&#xD;&#xA;YAHOO.util.Event.stopEvent&#xD;&#xA;YAHOO.util.Event.stopPropagation&#xD;&#xA;YAHOO.util.Event.stopEvent&#xD;&#xA;YAHOO.util.Anim&#xD;&#xA;YAHOO.util.Motion&#xD;&#xA;YAHOO.util.Connect.asyncRequest&#xD;&#xA;YAHOO.util.Connect.setForm&#xD;&#xA;YAHOO.util.Dom&#xD;&#xA;YAHOO.util.Event">
    <include name="yui\utilities.js" />
    <include name="yui-ext\yui-ext.js" />
    <include name="util\include.js" />
    <include name="prototype.js" />
    <include name="mask.js" />
    <include name="sigaa.js" />
    <include name="formatador.js" />
    <include name="ajax\ajax.js" />
    <include name="formutils.js" />
    <include name="paineis.js" />
    <include name="yui\container-min.js" />
    <include name="mensagens.js" />
  </target>
  <file name="mask.js" path="" />
  <file name="sigaa.js" path="" />
  <file name="formatador.js" path="" />
  <file name="ajax\ajax.js" path="ajax" />
  <file name="formutils.js" path="" />
  <file name="paineis.js" path="" />
  <file name="yui\container-min.js" path="yui" />
  <file name="mensagens.js" path="" />
  <file name="prototype.js" path="" />
  <file name="tooltip.js" path="" />
  <file name="overlib.js" path="" />
  <target name="SIPAC" file="$output\sipac-min.js" debug="False" shorthand="False" shorthand-list="YAHOO.util.Dom.setStyle&#xD;&#xA;YAHOO.util.Dom.getStyle&#xD;&#xA;YAHOO.util.Dom.getRegion&#xD;&#xA;YAHOO.util.Dom.getViewportHeight&#xD;&#xA;YAHOO.util.Dom.getViewportWidth&#xD;&#xA;YAHOO.util.Dom.get&#xD;&#xA;YAHOO.util.Dom.getXY&#xD;&#xA;YAHOO.util.Dom.setXY&#xD;&#xA;YAHOO.util.CustomEvent&#xD;&#xA;YAHOO.util.Event.addListener&#xD;&#xA;YAHOO.util.Event.getEvent&#xD;&#xA;YAHOO.util.Event.getTarget&#xD;&#xA;YAHOO.util.Event.preventDefault&#xD;&#xA;YAHOO.util.Event.stopEvent&#xD;&#xA;YAHOO.util.Event.stopPropagation&#xD;&#xA;YAHOO.util.Event.stopEvent&#xD;&#xA;YAHOO.util.Anim&#xD;&#xA;YAHOO.util.Motion&#xD;&#xA;YAHOO.util.Connect.asyncRequest&#xD;&#xA;YAHOO.util.Connect.setForm&#xD;&#xA;YAHOO.util.Dom&#xD;&#xA;YAHOO.util.Event">
    <include name="prototype.js" />
    <include name="yui\utilities.js" />
    <include name="yui-ext\yui-ext.js" />
    <include name="util\include.js" />
    <include name="yui\container-min.js" />
    <include name="overlib.js" />
    <include name="tooltip.js" />
  </target>
</project>