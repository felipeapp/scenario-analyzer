<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Alunos e Seus Respectivos Orientadores</h2>
<h:form>
	<table align="center" class="formulario" width="80%">
	<caption>Dados do Relatório</caption>
	<tbody>
		<tr>
			<th class="required">Curso:</th>
			<td>
				<h:selectOneMenu value="#{relatorioDiscente.idCurso}" id="curso" 
					immediate="true">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{relatorioDiscente.possiveisCursosCoordStricto}" />
				</h:selectOneMenu> 
			</td>
		</tr>
		<tr>
			<th class="required">Ordenar por:</th>
			<td>
				<h:selectOneRadio value="#{relatorioDiscente.ordenarPorOrientador }">
					<f:selectItem itemValue="false" itemLabel="Discentes"/>
					<f:selectItem itemValue="true" itemLabel="Orientadores"/>
				</h:selectOneRadio>
			</td>
		</tr>
	</tbody>
	<tfoot> 
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscente.gerarRelatorioAlunosRespecOrientadores}"/>
			<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" 
				onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"/>
		</td>
	</tr>
	</tfoot>
	</table>
	
	<div align="center" class="fontePequena">
		<span class="required">&nbsp;&nbsp;</span> Campos de preenchimento obrigatório.	
	</div>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


