<%@include file="/public/include/cabecalho.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>
<style>
<!--

div.ignoreCss {
	padding-top: 5px;
}

div.descricaoOperacao div.ignoreCss ul,ol {
	margin-left: 20px;
}

div.descricaoOperacao div.ignoreCss ul li {
	list-style-type: disc;
}

div.descricaoOperacao div.ignoreCss ol li {
	list-style-type: decimal;
}

div.ignoreCss strong, strong em {
	font-weight: bold;
}

div.ignoreCss em, em strong {
	font-style: italic;
}

-->
</style>
<f:view>
	<a4j:keepAlive beanName="inscricaoParticipantes"></a4j:keepAlive>
	<h2>Inscrição nos Cursos e Eventos de Extensão</h2>
	
	<div class="descricaoOperacao">
		<p><b>Para realizar a solicitação de inscrição nesta ação deve-se observar as instruções abaixo:</b></p>
		<div class="ignoreCss">${inscricaoParticipantes.obj.inscricaoAtividade.instrucoesInscricao}</div>
	</div>
	
	
	<c:if test="${inscricaoParticipantes.jaExisteInscricaoNestaAtividade}">
		<div class="descricaoOperacao">
			<p>
				<font color="green">
					Você já realizou uma inscrição para esta Ação de Extensão em <b><fmt:formatDate value="${inscricaoParticipantes.inscricaoAnterior.dataCadastro}" pattern="dd/MM/yyyy HH:mm"/></b>.<br/>
				Ao confirmar o cadastro atual, a inscrição do dia <b><fmt:formatDate value="${inscricaoParticipantes.inscricaoAnterior.dataCadastro}" pattern="dd/MM/yyyy HH:mm"/></b> será cancelada.
				</font>
			</p>
		</div>
	</c:if>
	
	<h:form id="form"  enctype="multipart/form-data">
		<table class="formulario" width="90%">
		<caption>Formulário de Inscrição</caption>
		<tbody>
			<tr>
				<td colspan="4" class="subFormulario">Dados Pessoais
				
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/indicator.gif" />
						</f:facet>
					</a4j:status>
				
				</td>
			</tr>
			
			<tr>	
				<th width="20%">Título da Ação: </th>
				<td colspan="3"><b><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.atividade.titulo}" /></b></td>				
			</tr>

			<tr>	
				<th width="20%">Tipo: </th>
				<td colspan="3"><b><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.atividade.tipoAtividadeExtensao.descricao}" /></b></td>				
			</tr>
			
			<tr>	
				<th width="20%">Título da Mini Atividade: </th>
				<td colspan="3"><b><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.titulo}" /></b></td>				
			</tr>

			<tr>	
				<th width="20%">Tipo da Mini Atividade: </th>
				<td colspan="3"><b><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.tipoSubAtividadeExtensao.descricao}" /></b></td>				
			</tr>
			
			<tr>	
				<th width="20%">Início: </th>
				<td colspan="3"><b><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.inicio}" /></b></td>				
			</tr>
			
			<tr>	
				<th width="20%">Fim: </th>
				<td colspan="3"><b><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.fim}" /></b></td>				
			</tr>
			
			<tr>	
				<th width="20%">Horário: </th>
				<td colspan="3"><b><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.horario}" /></b></td>				
			</tr>
			
			<tr>	
				<th width="20%">Carga Horária: </th>
				<td colspan="3"><b><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.cargaHoraria}" /></b></td>				
			</tr>
			
			<tr>	
				<th width="20%">Descrição: </th>
				<td colspan="3"><b><h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.descricao}" escape="false"/></b></td>				
			</tr>
			
			
			<tr>	
				<th width="20%">Estrangeiro: </th>
				<td colspan="3">
					<h:selectBooleanCheckbox immediate="true" id="checkEstrangeiro" value="#{inscricaoParticipantes.obj.internacional}" >
						<a4j:support id="suportCheck" actionListener="#{inscricaoParticipantes.novoCadastroParticipante}" event="onclick" reRender="form"/>
					</h:selectBooleanCheckbox>
				</td>				
			</tr>
			
			<c:if test="${!inscricaoParticipantes.obj.internacional}">
				<tr>
					<th ${ inscricaoParticipantes.obj.internacional ? '' : 'class="obrigatorio"' }>Informe seu CPF:</th>
					<td colspan="3" >
						<h:inputText value="#{inscricaoParticipantes.obj.cpf}" size="14" id="cpf"
								maxlength="14" onkeypress="formataCPF(this, event, null)">
							<f:converter converterId="convertCpf" />
							<f:param id="paraCpf" name ="type" value="cpf" />
						</h:inputText>&nbsp;&nbsp;
						<h:commandLink actionListener="#{inscricaoParticipantes.verificarInscricaoExistente}" id="lupaCpf">
							<a4j:support id="supportLupa" event="onclick" reRender="form,btnConfComAviso, btnConfSemAviso" />
							<h:graphicImage value="/img/buscar.gif" />
						</h:commandLink>
						<span class="info">(clique na lupa para buscar seus dados pelo CPF informado)</span> 
					</td>
				</tr>
			</c:if>
			<c:if test="${inscricaoParticipantes.obj.internacional}">
				<tr>
					<th ${ inscricaoParticipantes.obj.internacional ? 'class="obrigatorio"' : '' }>Passaporte:</th>
					<td colspan="3">
						<h:inputText value="#{inscricaoParticipantes.obj.passaporte}" maxlength="20" size="20" id="passaporte" />
						&nbsp;&nbsp;
						<h:commandLink actionListener="#{inscricaoParticipantes.verificarInscricaoExistente}" id="lupaPassaporte">
							<a4j:support id="supportPassaporte" event="onclick" reRender="form" />
							<h:graphicImage value="/img/buscar.gif" />
						</h:commandLink>
						<span class="info">(clique na lupa para buscar seus dados pelo Passaporte informado)</span> 
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="required">Nome Completo:</th>
				<td colspan="3">
					<h:inputText value="#{inscricaoParticipantes.obj.nome}" size="55" maxlength="60" 
							id="nome" disabled="#{!inscricaoParticipantes.novoCadastro}" />
				</td>
			</tr>
			<tr>
				<th class="required">Data de Nascimento:</th>
				<td colspan="3"><t:inputCalendar id="dataNascimento" value="#{inscricaoParticipantes.obj.dataNascimento}" 
							renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
							size="10" onkeypress="formatarMascara(this,event,'##/##/####')" maxlength="10" 
							popupTodayString="Hoje é" disabled="#{!inscricaoParticipantes.novoCadastro}">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
					<span style="color: gray">Ex.:(dd/mm/aaaa)</span>
				</td>
			</tr>
			<tr>
				<th>Instituição:</th>
				<td colspan="3">
					<h:inputText value="#{inscricaoParticipantes.obj.instituicao}" size="55" maxlength="60" 
							id="instituicao" disabled="#{!inscricaoParticipantes.novoCadastro}" />
				</td>
			</tr>
			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="4" class="subFormulario">Endereço</td>
			</tr>
			<tr class="linhaCep">
				<th class="required">CEP:</th>
				<td colspan="3">
					<h:inputText value="#{inscricaoParticipantes.obj.cep}" 
							maxlength="10" size="10" id="cep" 
							onkeyup="return formatarInteiro(this);"
							onblur="formataCEP(this, event, null); ConsultadorCep.consultar();"
							disabled="#{!inscricaoParticipantes.novoCadastro}" />
					<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
						<img src="/sigaa/img/buscar.gif" alt="Buscar Endereço" /></a> 
					<span class="info">(clique na lupa para buscar o endereço do CEP informado)</span> 
					<span id="cepIndicator" style="display: none;"><img src="/sigaa/img/indicator.gif" /> Buscando endereço... </span>			
				</td>
			</tr>
			<tr>
				<th class="required">Rua/Av.:</th>
				<td>
					<h:inputText value="#{inscricaoParticipantes.obj.logradouro}" id="logradouro" size="55"
							maxlength="60" disabled="#{!inscricaoParticipantes.novoCadastro}" />
				</td>
				<th class="required" width="10%">Número:</th>
				<td><h:inputText value="#{inscricaoParticipantes.obj.numero}" id="numero" maxlength="6" size="5" 
							onkeyup="formatarInteiro(this);" disabled="#{!inscricaoParticipantes.novoCadastro}" />
				</td>
			</tr>
			<tr>
				<th class="required">Bairro:</th>
				<td colspan="3">
					<h:inputText value="#{inscricaoParticipantes.obj.bairro}" id="bairro" size="40" 
							maxlength="45" disabled="#{!inscricaoParticipantes.novoCadastro}" /></td>
			</tr>
			<tr>
				<th class="required">UF:</th>
				<td><h:selectOneMenu value="#{inscricaoParticipantes.obj.unidadeFederativa.id}" id="uf" 
							disabled="#{!inscricaoParticipantes.novoCadastro}" immediate="true">
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
						<a4j:support event="onchange" reRender="municipio" action="#{inscricaoParticipantes.carregarMunicipios}"/>
					</h:selectOneMenu>
				</td>
				<th class="required">Município:</th>
				<td>
					<h:selectOneMenu value="#{inscricaoParticipantes.obj.municipio.id}" id="municipio" 
							disabled="#{!inscricaoParticipantes.novoCadastro}">
						<f:selectItems value="#{inscricaoParticipantes.municipiosEndereco}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="4" class="subFormulario">Contato</td>
			</tr>
			<tr>
				<th class="required">E-mail:</th>
				<td colspan="3">
					<h:inputText value="#{inscricaoParticipantes.obj.email}" id="email" size="40"
							maxlength="45" disabled="#{!inscricaoParticipantes.novoCadastro}" />
				</td>
			</tr>
			
			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>

			<tr>
				<td colspan="4" class="subFormulario">Arquivo</td>
			</tr>
			
			<c:set var="arquivoObrigatorio" value="${ inscricaoParticipantes.obj.inscricaoAtividade.envioArquivoObrigatorio ? 'required' : '' }" />
			<tr>
				<th class="${ arquivoObrigatorio }"> Descrição do Arquivo:</th>
				<td colspan="3">
					<h:inputText  id="descricao" value="#{inscricaoParticipantes.obj.descricaoArquivo}" size="83" maxlength="90"/>
				</td>
			</tr>
		
			<tr>
				<th class="${ arquivoObrigatorio }">Arquivo:</th>
				<td colspan="3">
					<t:inputFileUpload id="uFile" value="#{inscricaoParticipantes.obj.file}" storage="file" size="70"/>
				</td>
			</tr>
		
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">
						<h:commandButton value="Confirmar Inscrição" action="#{inscricaoParticipantes.criarInscricaoParticipanteSubAtividade}" 
							id="btnConfComAviso" rendered="#{inscricaoParticipantes.jaExisteInscricaoNestaAtividade}"
							onclick="return confirm('Deseja enviar sua inscrição? Verifique os dados digitados antes de continuar!');"
							onmouseup="alert('Prezado #{inscricaoParticipantes.obj.nome}, a sua inscrição anterior será cancelada em detrimento dessa nova inscrição.');" />
						<h:commandButton value="Confirmar Inscrição" action="#{inscricaoParticipantes.criarInscricaoParticipanteSubAtividade}" 
							id="btnConfSemAviso" rendered="#{!inscricaoParticipantes.jaExisteInscricaoNestaAtividade}"
							onclick="return confirm('Deseja enviar sua inscrição? Verifique os dados digitados antes de continuar!');" />
					&nbsp;
					<h:commandButton value="Cancelar" action="#{inscricaoParticipantes.cancelar}" onclick="#{confirm}" immediate="true" />
				</td>
			</tr>
		</tfoot>
		</table>
		
	</h:form>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena">Campos de preenchimento obrigatório.</span>
		<br /><br />
	</center>
	
	<br />
	<div style="margin: 0pt auto; width: 80%; text-align: center;">
		<a href="/sigaa/link/public/extensao/inscricoesOnline">&lt;&lt; voltar</a>
	</div>
	<br />
	
</f:view>

<script type="text/javascript">
	var posProcessamento = function() {
		$('form:municipio').value = $('form:municipio').options[0].value;
		$('form:uf').onchange();
	}
	ConsultadorCep.init('/sigaa/consultaCep','form:cep','form:logradouro','form:bairro','form:municipio','form:uf',posProcessamento);
</script>

<%@include file="/public/include/rodape.jsp" %>
