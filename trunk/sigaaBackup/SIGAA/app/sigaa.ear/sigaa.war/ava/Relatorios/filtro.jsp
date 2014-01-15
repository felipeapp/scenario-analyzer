<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Solicitação de Bolsa Auxílio </h2>

<f:view>
	<h:form id="form">
		<table class="formulario" width="80%">
		
			<caption>Relatório de acesso dos Discentes a Turma Virtual</caption>
			
			<tbody>
		
				<tr align="center">
					<td>
						Filtro:
							<h:selectOneMenu value="#{turmaVirtual.tipoRelatorio}">
								<f:selectItem itemLabel="Arquivos que foram baixados" itemValue="1" />
								<f:selectItem itemLabel="Discentes que acessaram a Turma Virtual" itemValue="2" />
								<f:selectItem itemLabel="Discentes que visualizaram as Tarefas da Turma" itemValue="3" />
							</h:selectOneMenu>
					</td>					
				</tr>
						
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						
						<h:commandButton value="Gerar relatório" action="#{turmaVirtual.gerarRelatorioAcessoDiscentesTurmaVirtual}" id="cadastrar"/>
						<h:commandButton value="Cancelar" action="#{turmaVirtual.cancelar}" id="cancelarOperacao" onclick="#{confirm}" />
						
					</td>
				</tr>
			</tfoot>
		</table>
		
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br> </center>
		
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>