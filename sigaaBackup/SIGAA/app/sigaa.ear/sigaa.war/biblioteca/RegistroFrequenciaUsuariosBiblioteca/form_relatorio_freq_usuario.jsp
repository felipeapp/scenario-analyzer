<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Consultar Movimentação Diária de Usuários </h2>
	
	<h:form>
		<table class="formulario" width="65%">
			<caption class="formulario">Dados da Busca</caption>
			
				<tr>
					<th>Biblioteca:</th>
					<td>
						<h:selectOneMenu value="#{registroFrequenciaUsuariosBibliotecaMBean.idBiblioteca}">
							<f:selectItem itemValue="#{registroFrequenciaUsuariosBibliotecaMBean.constanteTodasBibliotecas}" itemLabel="Todas"/>
							<f:selectItem itemValue="#{registroFrequenciaUsuariosBibliotecaMBean.constanteTodasBibliotecasSetoriais}" itemLabel="Todas as Setoriais"/>
							<f:selectItems value="#{abstractRelatorioBibliotecaMBean.bibliotecasCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Ano:</th>
					<td>
						<h:selectOneMenu value="#{registroFrequenciaUsuariosBibliotecaMBean.ano}">
							<f:selectItems value="#{abstractRelatorioBibliotecaMBean.anos}" />
						</h:selectOneMenu>
					</td>
				</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gerar Relatório" action="#{registroFrequenciaUsuariosBibliotecaMBean.gerarRelatorioAnual}" />
						<h:commandButton value="Cancelar"  action="#{registroFrequenciaUsuariosBibliotecaMBean.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br />
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>