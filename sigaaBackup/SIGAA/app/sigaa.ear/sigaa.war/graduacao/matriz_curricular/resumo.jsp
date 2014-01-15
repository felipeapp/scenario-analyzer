<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<style>
<!--
table.formulario th {font-weight: bold}
-->
</style>
	<h2 class="title">Matrizes Curriculares de Gradua��o &gt; Resumo</h2>

	<br>
	<table class="formulario" width="90%">
		<h:form id="formulario">
			<caption class="listagem">Resumo da Matriz Curricular</caption>
			<tr>
				<th width="30%">Curso:</th>
				<td  colspan="3"><h:outputText value="#{matrizCurricular.obj.curso.descricao}" /></td>
			</tr>
			<tr>
				<th width="30%">Campus:</th>
				<td  colspan="3"><h:outputText value="#{matrizCurricular.obj.campus.nome}" /></td>
			</tr>			
			<tr>
				<th>Turno:</th>
				<td colspan="3"><h:outputText value="#{matrizCurricular.obj.turno.descricao}" /></td>
			</tr>
			<tr>
				<th>Grau Acad�mico:</th>
				<td colspan="3"><h:outputText value="#{matrizCurricular.obj.grauAcademico.descricao}" /></td>
			</tr>

			<tr>
				<th>�nfase:</th>
				<td colspan="4">${matrizCurricular.obj.possuiEnfase ? matrizCurricular.obj.enfase.nome : 'Sem �nfase' }</td>
			</tr>
		
			<tr>
				<th>Regime Letivo:</th>
				<td colspan="3"><h:outputText value="#{matrizCurricular.obj.regimeLetivo.descricao}" /></td>
			</tr>
			<tr>
				<th>Sistema Curricular:</th>

				<td colspan="3"><h:outputText
					value="#{matrizCurricular.obj.tipoSistemaCurricular.descricao}" /></td>
			</tr>
			<tr>
				<th>Situa��o:</th>
				<td colspan="3"><h:outputText value="#{matrizCurricular.obj.situacao.descricao}" /></td>
			</tr>
			<tr>
				<th>Situa��o do Diploma:</th>
				<td colspan="3"><h:outputText value="#{matrizCurricular.obj.situacaoDiploma.descricao}" /></td>
			</tr>
			<tr>
				<th>C�digo INEP:</th>
				<td>
					<h:outputText id="codigoINEP" value="#{matrizCurricular.obj.codigoINEP}"/>
				</td>
			</tr>
			<tr>
				<th>In�cio Funcionamento:</th>
				<td><ufrn:format type="data"
					valor="${matrizCurricular.obj.dataInicioFuncionamento}" /></td>
				<c:if test="${matrizCurricular.obj.id != 0 and not empty matrizCurricular.obj.dataExtincao}">
					<th>Data Extin��o:</th>
					<td><ufrn:format type="data" valor="${matrizCurricular.obj.dataExtincao}" /></td>
				</c:if>
			</tr>
			<c:if test="${not matrizCurricular.obj.possuiHabilitacao }">
				<tr>
					<th>Habilita��o:</th>
					<td colspan="4">Sem habilita��o.</td>
				</tr>
			</c:if>
				<tr>
					<th>Ativa:</th>
					<td colspan="4"> ${matrizCurricular.obj.ativo ? "Sim" : "N�o" } </td>
				</tr>
				<tr>
					<th>Permite Cola��o de grau:</th>
					<td colspan="4"> ${matrizCurricular.obj.permiteColacaoGrau ? "Sim" : "N�o" } </td>
				</tr>
			<c:if test="${matrizCurricular.obj.possuiHabilitacao }">
				<tr>
					<td colspan="4">
					<table class="subFormulario" width="100%">
						<caption class="listagem">Dados da Habilita��o</caption>
						<tr>
							<th width="30%">Nome:</th>
							<td colspan="3"><h:outputText value="#{matrizCurricular.obj.habilitacao.nome}" /></td>
						</tr>
						<tr>
							<th>C�digo:</th>
							<td width="100px"><h:outputText value="#{matrizCurricular.obj.habilitacao.codigoIes}" /></td>
							<th width="120px">C�digo INEP:</th>
							<td><h:outputText value="#{matrizCurricular.obj.habilitacao.codigoHabilitacaoInep}" /></td>
						</tr>
						<tr>
							<th>Op��o Para Habilita��o:</th>
							<td><ufrn:format type="bool_sn"
								valor="${matrizCurricular.obj.habilitacao.opcaoParaHabilitacao}" /></td>
						</tr>
						<tr>
							<th>�rea Sesu:</th>
							<td colspan="3">
								<c:if test="${empty matrizCurricular.obj.habilitacao.areaSesu.descricao}">
									<h:outputText value="#{matrizCurricular.obj.habilitacao.areaSesu.descricao}" />
								</c:if>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="3">
				<table width="100%" class="subFormulario">
					<caption>Autoriza��o de Funcionamento</caption>
					<tr>
						<th width="30%">Ato Normativo:</th>
						<td><h:outputText value="#{matrizCurricular.obj.autorizacaoAtoNormativo}" />
						</td>
					</tr>
					<tr>
						<th>Data do Ato Normativo:</th>
						<td>
						<ufrn:format type="data" valor="${matrizCurricular.obj.autorizacaoAtoData}" />
						</td>
					</tr>
					<tr>
						<th>Data da Publica��o:</th>
						<td>
						<ufrn:format type="data" valor="${matrizCurricular.obj.autorizacaoPublicacao}" />
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="#{matrizCurricular.confirmButton }" id="btnConfirmar" action="#{matrizCurricular.cadastrar}" /> 
						<c:if test="${matrizCurricular.confirmButton != 'Remover'}">
							<h:commandButton value="<< Dados da Matriz" id="btnVoltar" action="#{matrizCurricular.voltarDadosMatriz}" /> 
							<h:commandButton value="<< Dados da Habilita��o" id="btnVoltarHabilitacao" 
								action="#{matrizCurricular.voltarDadosHabilitacao}" rendered="#{matrizCurricular.obj.possuiHabilitacao }"/>
						</c:if>
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="btnCancelar" action="#{matrizCurricular.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
