<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>	
	
<table class="visualizacao" style="width: 80%" >
	<caption>Resumo</caption>
	<tr>
		<th width="3%" style="text-align: right;" nowrap="nowrap">CH Total Módulos (Obrigatórias):</th>		
		<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.chTotalModulos}"/> ch</td>
	</tr>
	<tr>
		<th width="3%" style="text-align: right;" nowrap="nowrap">Carga horária Complementar:</th>				
		<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.chTotalDisciplinasComplementares}"/> ch</td>
	</tr>		
</table>