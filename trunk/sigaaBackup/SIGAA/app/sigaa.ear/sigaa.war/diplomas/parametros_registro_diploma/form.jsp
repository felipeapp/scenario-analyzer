<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> > Parâmetros do Registro de Diplomas</h2>
	<h:form id="form">
	<a4j:keepAlive beanName="parametrosRegistroDiploma"></a4j:keepAlive>
	<div class="descricaoOperacao">Digite os valores utilizados no registro
		de diplomas. Para os diretores do ${ configSistema['siglaUnidadeGestoraGraduacao'] } e DRED, selecione da lista que
		será exibida ao digitar um nome.</div>
		<table class="formulario" width="90%">
			<caption>Informe os Valores dos Parâmetros</caption>
			<tbody>
			<tr>
				<th class="required">Ano-Semestre inicial do registro de diplomas: </th>
				<td>
					<h:inputText value="#{ parametrosRegistroDiploma.anoInicioRegistro.valor }" 
					 size="4" maxlength="4" onkeyup="return formatarInteiro(this);" id="ano"/> - 
					<h:inputText value="#{ parametrosRegistroDiploma.semestreInicioRegistro.valor }" 
					 size="1" maxlength="1" onkeyup="return formatarInteiro(this);"  id="semestre"/>
				</td>
			</tr>
			<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO} %>">
				<tr>
					<th class="required" >Diretor do ${ configSistema['siglaUnidadeGestoraGraduacao'] }:</th>
					<td>
						<h:inputText value="#{parametrosRegistroDiploma.pessoaDiretorDAE.nome}" id="nomeDiretorDae" size="50"/>
						<rich:suggestionbox for="nomeDiretorDae" width="450" height="100" minChars="3" id="suggestionNomeDiretorDae" 
							suggestionAction="#{parametrosRegistroDiploma.autocompleteNomePessoa}" var="_diretorDae" 
							fetchValue="#{_diretorDae.nome}">	
							<h:column>
								<h:outputText value="#{_diretorDae.nome}" />
							</h:column>							        
					        <f:param name="apenasAtivos" value="true" />
				        	<a4j:support event="onselect">
								<f:setPropertyActionListener value="#{_diretorDae.id}" target="#{parametrosRegistroDiploma.pessoaDiretorDAE.id}" />
							</a4j:support>
						</rich:suggestionbox>
					</td>
				</tr>
				<tr>
					<th class="required">Diretor do DRED: </th>
					<td>
						<h:inputText value="#{parametrosRegistroDiploma.pessoaDiretorDRED.nome}" id="nomediretorDred" size="50"/>
						<rich:suggestionbox for="nomediretorDred" width="450" height="100" minChars="3" id="suggestionNomediretorDred" 
							suggestionAction="#{parametrosRegistroDiploma.autocompleteNomePessoa}" var="_diretorDred" 
							fetchValue="#{_diretorDred.nome}">	
							<h:column>
								<h:outputText value="#{_diretorDred.nome}" />
							</h:column>							        
					        <f:param name="apenasAtivos" value="true" />
				        	<a4j:support event="onselect">
								<f:setPropertyActionListener value="#{_diretorDred.id}" target="#{parametrosRegistroDiploma.pessoaDiretorDRED.id}" />
							</a4j:support>
						</rich:suggestionbox>
					</td>
				</tr>
			</ufrn:checkRole>
			<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_DIPLOMAS_LATO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO} %>">
				<tr>
					<th class="required" >Pró-Reitor de Pós-Graduação:</th>
					<td>
						<h:inputText value="#{parametrosRegistroDiploma.pessoaProReitorPosGraduacao.nome}" id="nomeProReitorPosGraduacao" size="50"/>
						<rich:suggestionbox for="nomeProReitorPosGraduacao" width="450" height="100" minChars="3" id="suggestionNomeProReitorPosGraduacao" 
							suggestionAction="#{parametrosRegistroDiploma.autocompleteNomePessoa}" var="_proReitor" 
							fetchValue="#{_proReitor.nome}">	
							<h:column>
								<h:outputText value="#{_proReitor.nome}" />
							</h:column>							        
					        <f:param name="apenasAtivos" value="true" />
				        	<a4j:support event="onselect">
								<f:setPropertyActionListener value="#{_proReitor.id}" target="#{parametrosRegistroDiploma.pessoaProReitorPosGraduacao.id}" />
							</a4j:support>
						</rich:suggestionbox>
					</td>
				</tr>
			</ufrn:checkRole>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="#{ parametrosRegistroDiploma.confirmButton }" action="#{ parametrosRegistroDiploma.cadastrar }" id="btnCadastrar"/>
					<h:commandButton value="Cancelar" action="#{ parametrosRegistroDiploma.cancelar }" onclick="#{ confirm }" id="btnCancelar"/>
				</td></tr>
			</tfoot>
		</table>

		<br />
		<center><img src="/shared/img/required.gif" style="vertical-align: middle;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
		<br />

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>