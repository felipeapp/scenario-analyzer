<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Relatório de Matrículas Projetadas</h2>
	<h:form id="form">
	<h:outputText value="#{relatorioCurso.create}"></h:outputText>
    <table class="formulario" width="40%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<th>Período: </th>
					<td align="left"><h:selectOneMenu value="#{relatorioCurso.ano}" id="anoIni">
	 						<f:selectItems value="#{relatorioCurso.anos}" />
						</h:selectOneMenu> 
						a						
						<h:selectOneMenu value="#{relatorioCurso.anoFim}" id="anoFim">
				   			<f:selectItems value="#{relatorioCurso.anos}" />
				   		</h:selectOneMenu>
					</td>
				</tr>
			</tbody>														
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" action="#{relatorioCurso.geraRelatorioMatriculasProjetadasCurso}"/> 
						<h:commandButton value="Cancelar" action="#{relatorioCurso.cancelar}" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
	</table>
													
	</h:form>
  <br />
  <br />
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
