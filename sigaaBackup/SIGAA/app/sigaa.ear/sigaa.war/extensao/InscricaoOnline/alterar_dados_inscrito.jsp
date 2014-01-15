<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2>Alterar Dados da Inscrição</h2>
	
	<h:form id="formAlteraDadosIncritos">
	
	<%-- Esse aqui é o MBean --%>
	<a4j:keepAlive beanName="inscricaoAtividade"></a4j:keepAlive>
	
	<table class="formulario" style="width: 90%;">
		<caption>Informações do Inscrito</caption>
		<tbody>

			<tr>
				<td colspan="2" class="subFormulario">Dados Pessoais</td>
			</tr>
			<c:if test="${ inscricaoAtividade.participante.passaporte == null}">
				<tr>
					<th width="20%">CPF:</th>
					<td><ufrn:format type="cpf_cnpj" valor="${inscricaoAtividade.participante.cpf}"/></td>
				</tr>
			</c:if>
			<c:if test="${inscricaoAtividade.participante.passaporte != null}">
				<tr>
					<th width="20%">Passaporte:</th>
					<td>${inscricaoAtividade.participante.passaporte}</td>
				</tr>
			</c:if>
			<tr>
				<th class="required">Nome Completo:</th>
				<td> <h:inputText value="#{inscricaoAtividade.participante.nome}" size="60" maxlength="100"></h:inputText> </td>
			</tr>
			<tr>
				<th class="required">Data de Nascimento:</th>
				<td>
					<t:inputCalendar value="#{inscricaoAtividade.participante.dataNascimento}" 
						size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
						renderPopupButtonAsImage="true"
						onkeypress="return formataData(this, event)" >
						<f:converter converterId="convertData" />
					</t:inputCalendar>	
				</td>
			</tr>
			<tr>
				<th>Instituição:</th>
				<td> <h:inputText value="#{inscricaoAtividade.participante.instituicao }" size="55" maxlength="60"></h:inputText> </td>
			</tr>
			
			<tr>
				<th class="required">CEP:</th>
				<td>
					<h:inputText value="#{inscricaoAtividade.participante.cep}" 
							maxlength="10" size="10" id="cep" 
							onkeyup="return formatarInteiro(this);"
							onblur="formataCEP(this, event, null);"/>	
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Endereço</td>
			</tr>
			<tr>
				<th class="required">Rua/Av.:</th>
				<td> <h:inputText value="#{inscricaoAtividade.participante.logradouro }" size="55" maxlength="60" /> 
					, <i>Nº.</i> 
					<h:inputText value="#{inscricaoAtividade.participante.numero }" maxlength="6" size="5"  /> 
				</td>
			</tr>
			<tr>
				<th class="required">Bairro:</th>
				<td> <h:inputText value="#{inscricaoAtividade.participante.bairro}" size="60" maxlength="100" /> </td>
			</tr>
			
			<tr>
				<th class="required">UF:</th>
				<td>  
					<h:selectOneMenu value="#{inscricaoAtividade.participante.unidadeFederativa.id}" id="uf" >
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
						<a4j:support event="onchange" reRender="municipio" action="#{inscricaoAtividade.carregarMunicipios}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="required">Município:</th>
				<td>  
					<h:selectOneMenu value="#{inscricaoAtividade.participante.municipio.id}" id="municipio" >
						<f:selectItems value="#{inscricaoAtividade.municipiosEndereco}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Contato</td>
			</tr>
			<tr>
				<th class="required">E-mail:</th>
				<td><h:inputText value="#{inscricaoAtividade.participante.email}" size="30" maxlength="50" /></td>
			</tr>
		</tbody>
		
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="cmdAlteraDadosInscritos" value="Alterar Dados da Inscrição" action="#{inscricaoAtividade.alterarDadosInscrito}" />
					<h:commandButton id="cmdCancelar" value="Cancelar" action="#{inscricaoAtividade.telaInscritos}" onclick="#{confirm}"  immediate="true" />
				</td>
			</tr>
		</tfoot>
		
	</table>
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>