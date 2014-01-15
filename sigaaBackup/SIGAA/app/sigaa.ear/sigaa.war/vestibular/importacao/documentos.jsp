<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Impress�o dos Relat�rios e Documentos para Cadastramento de Discentes</h2>

	<div class="descricaoOperacao">
		<p><b>Caro Usu�rio,</b></p>
		<p>Atrav�s deste formul�rio, voc� poder� gerar um dos seguintes conjuntos de documentos:
		<ul> 
			<li><b>Etiquetas:</b> Etiquetas dos Candidatos Aprovados, utilizadas para a identifica��o da pasta de documentos do aluno cadastrado.</li> 
			<li><b>Documentos para cadastro de Discentes:</b> Nessa op��o s�o gerados os documentos necess�rios para o cadastramento do discente na institui��o.</li>
			<li><b>Lista de Convoca��o de Aprovados: </b>Ser� gerada uma lista de candidatos serem convocados para o preenchimento das vagas, na referida chamada, informando nome e inscri��o do candidato, agrupados por curso. </li>
		</ul>
	</div>

	<h:form id="form">
		<a4j:keepAlive beanName="documentosDiscentesImportadosMBean"></a4j:keepAlive>
		<table align="center" class="formulario" width="100%">
			<caption class="listagem">Dados dos Documentos</caption>
			<tr>
				<th width="20%" class="obrigatorio">Documento: </th>
				<td style="padding-left: 5px;">
					<h:selectOneMenu value="#{documentosDiscentesImportadosMBean.idRelatorio}" id="idRelario">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{documentosDiscentesImportadosMBean.relatoriosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Processo de Importa��o: </th>
				<td width="70%" style="padding-left: 5px;">
					<h:selectOneMenu id="processoSeletivo" immediate="true"
						value="#{documentosDiscentesImportadosMBean.idImportacao}"
						valueChangeListener="#{documentosDiscentesImportadosMBean.carregarMatrizes}"
						onchange="submit()" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{documentosDiscentesImportadosMBean.allImportacaoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Matriz(es) Curricular(es): </th>
				<td width="70%;">
					<h:panelGrid id="cursoPanel" > 
						<h:selectManyMenu id="curso" value="#{documentosDiscentesImportadosMBean.idsMatrizSelecionadas}" 
							rendered="#{not empty documentosDiscentesImportadosMBean.matrizesCombo}" style="width: 850px !important;">
							<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
							<f:selectItems value="#{documentosDiscentesImportadosMBean.matrizesCombo}"/>
						</h:selectManyMenu>
						<h:outputText value="N�o h� cursos com convaca��o de candidatos para o Processo Seletivo selecionado" 
							rendered="#{empty documentosDiscentesImportadosMBean.matrizesCombo && documentosDiscentesImportadosMBean.idImportacao > 0}"/>
						<h:outputText value="Selecione um Processo Seletivo"
							rendered="#{documentosDiscentesImportadosMBean.idImportacao == 0}"/>
					</h:panelGrid>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio" action="#{documentosDiscentesImportadosMBean.gerarRelatorio}" /> 
						<h:commandButton value="Cancelar" action="#{documentosDiscentesImportadosMBean.cancelar}" id="cancelar" onclick="#{confirm}"/></td>
				</tr>
			</tfoot>
		</table>
		<br />
			<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	<script>
	// Script necess�rio para que no IE o tamanho da caixa de sele��o de cursos seja adequado (bug do IE).
	$('form:curso').size = 8;
	</script>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>