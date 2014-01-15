<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"> </script>
<script>
/* Máscaras ER */  
function mascara(o,f){  
    v_obj=o  
    v_fun=f  
    setTimeout("execmascara()",1)  
}  
function execmascara(){  
    v_obj.value=v_fun(v_obj.value)  
}  
function mtel(v){  
    v=v.replace(/\D/g,"");             //Remove tudo o que não é dígito  
    v=v.replace(/(\d)(\d{4})$/,"$1-$2");    //Coloca hífen entre o quarto e o quinto dígitos  
    return v;  
}  
</script>
<f:view>
	<a4j:keepAlive beanName="convenioEstagioMBean"/>
	<h2> <ufrn:subSistema /> &gt; Solicitar Convênio de Estágio</h2>
	
	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Através dessa tela, você poderá Solicitar o Cadastro de Convênio de Estágio. Essa solicitação será enviada a <b>PROPLAN</b>,
		onde será analisada, e podendo ser <b>APROVADA</b> ou <b>RECUSADA</b>.</p>
		<p>Uma vez <b>APROVADA</b>, pode-se cadastrar as ofertas de estágio através do Portal do Coordenador de Curso de Graduação no menu 
		<b>Estágio -> Cadastrar Oferta de Estágio.</b></p>
	</div>

	<h:form id="form">
		<table class="formulario" width="80%">
			<caption>Solicitação de Convênio de Estágio</caption>
			<tr>
				<th class="required" valign="top">Tipo do Convênio:</th>
				<td colspan="3">
					<table>														
						<c:forEach items="#{convenioEstagioMBean.listaTiposConvenio}" var="tipo">
							<tr>
								<td>
									<input type="radio" name="tipoConvenio" value="${tipo.id}" id="tipoConvenio${tipo.id}" class="noborder" 
										${convenioEstagioMBean.obj.tipoConvenio.id == tipo.id ? 'checked' : ''}>
										<label for="tipoConvenio${tipo.id}">${tipo.descricao}</label>
									</input>
								</td>
								<td>							
									<ufrn:help>${tipo.textoDetalhe}</ufrn:help>
								</td>
							</tr>
						</c:forEach>
					</table>					
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" valign="top">Tipo de Oferta de Vaga:</th>
				<td colspan="3">
					<h:selectOneMenu value="#{convenioEstagioMBean.obj.tipoOfertaVaga}" id="tipoOfertaVaga">
						<f:selectItems value="#{convenioEstagioMBean.tipoOfertaVagaCombo}"/> 
					</h:selectOneMenu>				
				</td>
			</tr>
			<tr>
				<td colspan="4" class="subFormulario">Dados do Concedente</td>
			</tr>			
			
			<a4j:region>
				<tr>	
					<th><h:selectBooleanCheckbox value="#{convenioEstagioMBean.obj.orgaoFederal}" id="orgaoFederal"/></th>	
					<td colspan="3">
					    <label for="form:orgaoFederal">O Concedente é um Órgão Federal</label>
					</td>
				</tr>
				<tr>	
					<th><h:selectBooleanCheckbox value="#{convenioEstagioMBean.obj.agenteIntegrador}" id="agenteIntegrador"/></th>	
					<td colspan="3">
					    <label for="form:agenteIntegrador">O Concedente é um Agente de Integração</label>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">CPF/CNPJ:</th>
					<td colspan="3">						
						<h:inputText id="campocnpj" onkeypress="return formataCNPJ(this, event, null)"	value="#{convenioEstagioMBean.obj.concedente.pessoa.cpf_cnpj}" size="19" maxlength="18">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cnpj" />
							<a4j:support actionListener="#{convenioEstagioMBean.buscarCNPJ}" event="onblur" reRender="nome, endCEP, tipoLogradouro, logradouro, endBairro,
							endComplemento, ufEnd, endMunicipio, telFixoDDD, telFixoNumero, telCelDDD, telCelNumero" oncomplete="inicializarCEP();"/>
						</h:inputText>											
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td colspan="3">																																				
						<h:inputText id="nome" value="#{convenioEstagioMBean.obj.concedente.pessoa.nome}" onkeyup="CAPS(this);" size="60" maxlength="120"
							disabled="#{!convenioEstagioMBean.podeAlterarNomeConcedente}"/>
					</td>
				</tr>
				<tr class="linhaCep">
					<th class="obrigatorio">CEP:</th>
					<td colspan="3">
						<h:inputText value="#{convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.cep}" onkeyup="return formataCEP(this, event, null);"
							maxlength="10" size="10" id="endCEP" onblur="formataCEP(this, event, null); ConsultadorCep.consultar();"/>
						<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
							<img src="/sigaa/img/buscar.gif"/>
						</a>
						<span class="info">(clique na lupa para buscar o endereço do CEP informado)</span>
						<span id="cepIndicator" style="display: none;">
							<img src="/sigaa/img/indicator.gif"/> Buscando endereço...
						</span>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Logradouro:</th>
					<td colspan="3">
						<h:selectOneMenu value="#{convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.tipoLogradouro.id}"
							id="tipoLogradouro">
							<f:selectItems value="#{tipoLogradouro.allCombo}" />
						</h:selectOneMenu>
						<h:inputText value="#{convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.logradouro }"
							maxlength="70" id="logradouro" size="50"/>
							
						N.&deg;:
						<h:inputText value="#{convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.numero}" maxlength="8" size="10"
						id="endNumero" />
					</td>
				</tr>

				<tr>
					<th class="obrigatorio">Bairro:</th>
					<td><h:inputText value="#{convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.bairro}" maxlength="60" size="30"
						id="endBairro"/></td>
					<th>Complemento:</th>
					<td><h:inputText value="#{convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.complemento}" maxlength="80" size="20"
						id="endComplemento" /></td>
				</tr>

				<tr>
					<a4j:region>
						<th class="obrigatorio">UF:</th>
						<td>
							<h:selectOneMenu value="#{convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.unidadeFederativa.id}" id="ufEnd"
								valueChangeListener="#{convenioEstagioMBean.carregarMunicipios }">
								<f:selectItems value="#{unidadeFederativa.allCombo}" />
								<a4j:support event="onchange" reRender="endMunicipio" />
							</h:selectOneMenu>
						</td>
					</a4j:region>
					<th class="obrigatorio">Município:</th>
					<td>
						<h:selectOneMenu id="endMunicipio" value="#{convenioEstagioMBean.obj.concedente.pessoa.enderecoContato.municipio.id}">
							<f:selectItems value="#{convenioEstagioMBean.municipiosEndereco}" />
						</h:selectOneMenu>
					</td>
				</tr>

				<tr>
					<th>Tel. Fixo:</th>
					<td>(<h:inputText value="#{convenioEstagioMBean.obj.concedente.pessoa.codigoAreaNacionalTelefoneFixo}"
						maxlength="2" size="2" id="telFixoDDD" onkeyup="return formatarInteiro(this);" />)
						 <h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" value="#{convenioEstagioMBean.obj.concedente.pessoa.telefone}" maxlength="9" size="9" id="telFixoNumero" /></td>
					<th>Tel. Celular:</th>
					<td>(<h:inputText onkeyup="return formatarInteiro(this);"  
					    value="#{convenioEstagioMBean.obj.concedente.pessoa.codigoAreaNacionalTelefoneCelular}" maxlength="2" size="2"
						id="telCelDDD" />) 
						<h:inputText id="telCelNumero" onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" value="#{convenioEstagioMBean.obj.concedente.pessoa.celular}" maxlength="10" size="10" />
					</td>
				</tr>
 	
			</a4j:region>
												
			<tr>
				<td colspan="4" class="subFormulario">Dados do Responsável por Estágios no Concedente</td>
			</tr>	
		
			<a4j:region id="dadosResponsavel">
				<tr>
					<th class="obrigatorio">CPF:</th>
					<td colspan="3">						
						<h:inputText id="cpf" onkeypress="return formataCPF(this, event, null)"	value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.cpf_cnpj}" size="19" maxlength="14">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
							<a4j:support actionListener="#{convenioEstagioMBean.buscarCPF}" event="onblur" reRender="cpf, responsavel, rg, orgaoExpedicao, ufRG, expedicao, email"/>
						</h:inputText>					
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td colspan="3">																																				
						<h:inputText id="responsavel" value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.nome}" onkeyup="CAPS(this);" 
						size="60" maxlength="120" disabled="#{!convenioEstagioMBean.podeAlterarNomeResponsavel}"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">RG:</th>
					<td colspan="3">
						<h:inputText value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.identidade.numero}" id="rg"
						size="15" maxlength="15" onkeyup="return formatarInteiro(this);" disabled="#{!convenioEstagioMBean.podeAlterarNomeResponsavel}" />
						
						Órgão de Expedição: <span class="obrigatorio">&nbsp;</span>
						<h:inputText value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.identidade.orgaoExpedicao}"
						 size="10" id="orgaoExpedicao" maxlength="20" disabled="#{!convenioEstagioMBean.podeAlterarNomeResponsavel}" />
					</td>
				</tr>								
				<tr>
					<th class="obrigatorio">Cargo:</th>
					<td colspan="3">
						<h:inputText id="funcao" value="#{convenioEstagioMBean.concedenteEstagioPessoa.cargo}" onkeyup="CAPS(this);" size="60" maxlength="100" />
					</td>
				</tr>			
				<tr>
					<th>E-mail:</th>
					<td colspan="3">
						<h:inputText id="email" value="#{convenioEstagioMBean.concedenteEstagioPessoa.pessoa.email}" size="60" maxlength="100" />
					</td>
				</tr>			
			</a4j:region>																											
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="Cancelar" action="#{convenioEstagioMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancel"/>
						<h:commandButton value="Próximo >>" action="#{convenioEstagioMBean.proximoPasso}" id="btProximo"/>
					</td>
				</tr>
			</tfoot>
		</table>
	
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">

function inicializarCEP(){
	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro', 'form:endBairro', 'form:endMunicipio', 'form:ufEnd', function() {
		$('form:ufEnd').onchange();
	} );
}

inicializarCEP();

</script>