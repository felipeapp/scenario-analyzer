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
<h2> <ufrn:subSistema /> &gt; Cadastrar Novo Aluno </h2>

<a4j:keepAlive beanName="discenteInfantilMBean" />
<h:form id="form">
	<table class="formulario" width="90%">
		<caption>Dados do Aluno</caption>
		<tr>
			<th width="15%">CPF:</th>
			<td>
				<h:inputText id="cpfDiscente" value="#{discenteInfantilMBean.obj.pessoa.cpf_cnpj}" size="14" maxlength="14" 
						onkeypress="return formataCPF(this, event, null)">
					<f:converter converterId="convertCpf" />
					<a4j:support event="onblur" action="#{discenteInfantilMBean.carregarDadosDiscente}" 
						reRender="rgDiscente, nome, nomeMae, nomePai, sexo, nascimento, endCEP, 
								  tipoLogradouro, logradouro, endNumero, endBairro, endComplemento, 
								  ufEnd, endMunicipio, telFixoNumero"/>
				</h:inputText>	
			</td>
			<th>RG:</th>
			<td>
				<h:inputText id="rgDiscente" value="#{discenteInfantilMBean.obj.pessoa.identidade.numero}" size="14" maxlength="14" onkeyup="return formatarInteiro(this);"/> 
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Nome:</th>
			<td colspan="3">
				<h:inputText id="nome" value="#{discenteInfantilMBean.obj.pessoa.nome}" size="80" maxlength="100" />
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Nome da Mãe:</th>
			<td colspan="3">
				<h:inputText value="#{discenteInfantilMBean.obj.pessoa.nomeMae}" size="80" maxlength="100" id="nomeMae" />
			</td>
		</tr>
		<tr>
			<th>Nome do Pai:</th>
			<td colspan="3">
				<h:inputText value="#{discenteInfantilMBean.obj.pessoa.nomePai}" size="80" maxlength="100" id="nomePai" />
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Sexo:</th>
			<td>
				<h:selectOneRadio value="#{discenteInfantilMBean.obj.pessoa.sexo}" id="sexo" style="border:0px;">
					<f:selectItem itemValue="M" itemLabel="Masculino" />
					<f:selectItem itemValue="F" itemLabel="Feminino" />
				</h:selectOneRadio>
			</td>
			<th class="obrigatorio">Data de Nascimento:</th>
			<td>
				<t:inputCalendar value="#{discenteInfantilMBean.obj.pessoa.dataNascimento}" size="10" maxlength="10" onkeypress="return formataData(this,event);"
					id="nascimento" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" >
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Renda Familiar:</th>
			<td>
				<h:selectOneMenu value="#{discenteInfantilMBean.obj.rendaFamiliar.id}" id="rendaFamiliar" >
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
					<f:selectItems value="#{rendaFamiliarMBean.allCombo}"/>
				</h:selectOneMenu>
			</td>
			<th class="obrigatorio">Turma do Aluno:</th>
			<td>
				<h:selectOneMenu value="#{discenteInfantilMBean.obj.periodoAtual}" id="nivelInfantil" >
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
					<f:selectItems value="#{discenteInfantilMBean.niveisCombo}"/>
				</h:selectOneMenu>
			</td>
			
		</tr>
		<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
					<caption>Naturalidade</caption>
					<tr>
						<th width="15%">País:</th>
						<td>
							<h:selectOneMenu value="#{discenteInfantilMBean.obj.pessoa.pais.id}" id="naturPais"
								valueChangeListener="#{discenteInfantilMBean.alterarPais}" onchange="submit()">
								<f:selectItems value="#{pais.allCombo}" />
							</h:selectOneMenu>
						</td>
						<th width="15%"><c:if test="${discenteInfantilMBean.brasil}">UF:</c:if></th>
						<td>
							<c:if test="${discenteInfantilMBean.brasil}">
								<h:selectOneMenu value="#{discenteInfantilMBean.obj.pessoa.unidadeFederativa.id}" id="ufIdNatur" 
									onchange="submit()" valueChangeListener="#{discenteInfantilMBean.carregarMunicipios }" rendered="#{discenteInfantilMBean.brasil}">
									<f:selectItems value="#{unidadeFederativa.allCombo}" />
								</h:selectOneMenu>
							</c:if>
						</td>
					</tr>
					<tr>
						<th>Município:</th>
						<td colspan="3">
							<c:if test="${discenteInfantilMBean.brasil}">
								<h:selectOneMenu value="#{discenteInfantilMBean.obj.pessoa.municipio.id}" id="naturMunicipio" rendered="#{discenteInfantilMBean.brasil}">
									<f:selectItems value="#{discenteInfantilMBean.municipiosNaturalidade}" />
								</h:selectOneMenu>
							</c:if> 
							<c:if test="${not discenteInfantilMBean.brasil}">
								<h:inputText value="#{discenteInfantilMBean.obj.pessoa.municipioNaturalidadeOutro}" id="naturMunicipioOutros" size="80" maxlength="80" />
							</c:if>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="4" >
				<table width="100%" class="subFormulario">
					<caption>Endereço</caption>
					<tr class="linhaCep">
						<th width="15%" class="obrigatorio">CEP:</th>
						<td colspan="3">
							<h:inputText value="#{discenteInfantilMBean.obj.pessoa.enderecoContato.cep}" maxlength="10" size="10" id="endCEP"
								onkeyup="return formatarInteiro(this);" onblur="formataCEP(this, event, null); ConsultadorCep.consultar();" />
							<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
								<img src="/sigaa/img/buscar.gif" alt="" title="Busca o endereço do CEP informado" /> 
							</a> 
							<span class="info">(clique na lupa para buscar o endereço do CEP informado)</span> 
							<span id="cepIndicator" style="display: none;"> 
							<img src="/sigaa/img/indicator.gif" alt="" /> Buscando... </span>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Logradouro:</th>
						<td colspan="3">
							<h:selectOneMenu value="#{discenteInfantilMBean.obj.pessoa.enderecoContato.tipoLogradouro.id}" id="tipoLogradouro">
								<f:selectItems value="#{tipoLogradouro.allCombo}" />
							</h:selectOneMenu> 
							<h:inputText value="#{discenteInfantilMBean.obj.pessoa.enderecoContato.logradouro }" maxlength="60" id="logradouro" size="60" />
						</td>
						<th class="obrigatorio">N.&deg;:</th>
						<td>
							<h:inputText value="#{discenteInfantilMBean.obj.pessoa.enderecoContato.numero}" maxlength="8" size="6" id="endNumero" onkeypress="return formatarInteiro(this);" />
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Bairro:</th>
						<td>
							<h:inputText value="#{discenteInfantilMBean.obj.pessoa.enderecoContato.bairro}" maxlength="20" size="20" id="endBairro" />
						</td>
						<th>Complemento:</th>
						<td>
							<h:inputText value="#{discenteInfantilMBean.obj.pessoa.enderecoContato.complemento}"	maxlength="80" size="20" id="endComplemento" />
						</td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<th class="obrigatorio">UF:</th>
						<td>
							<h:selectOneMenu value="#{discenteInfantilMBean.obj.pessoa.enderecoContato.unidadeFederativa.id}" id="ufEnd" onchange="submit()"
									valueChangeListener="#{discenteInfantilMBean.carregarMunicipios }">
								<f:selectItems value="#{unidadeFederativa.allCombo}" />
							</h:selectOneMenu>
						</td>
						<th class="obrigatorio">Município:</th>
						<td>
							<h:selectOneMenu value="#{discenteInfantilMBean.obj.pessoa.enderecoContato.municipio.id}" id="endMunicipio">
								<f:selectItems value="#{discenteInfantilMBean.municipiosEndereco}" />
							</h:selectOneMenu>
						</td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<th>Tel. Fixo:</th>
						<td colspan="5">
							(<h:inputText onkeyup="return formatarInteiro(this);" value="#{discenteInfantilMBean.obj.pessoa.codigoAreaNacionalTelefoneFixo}" maxlength="2" size="1" id="telFixoDDD" />) 
							<h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="telFixoNumero" value="#{discenteInfantilMBean.obj.pessoa.telefone}" maxlength="9" size="9" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
					<caption>Informações de Saúde</caption>
					<tr>
						<th width="15%">Alergias:</th>
						<td colspan="3">
							<h:inputText id="alergias" value="#{discenteInfantilMBean.obj.alergias}" size="80" maxlength="100" />
							<ufrn:help>Alergias a alimentos e/ou medicamentos</ufrn:help>
						</td>
					</tr>
					<tr>
						<th >Doenças:</th>
						<td colspan="3">
							<h:inputText id="doencas" value="#{discenteInfantilMBean.obj.doencas}" size="80" maxlength="100" />
							<ufrn:help>Doenças que a criança já teve</ufrn:help>
						</td>
					</tr>
					<tr>
						<th >Plano de Saúde:</th>
						<td colspan="3">
							<h:inputText id="planoSaude" value="#{discenteInfantilMBean.obj.planoSaude}" size="80" maxlength="100" />
						</td>
					</tr>
					<tr>
						<th >Pronto Socorro:</th>
						<td colspan="3">
							<h:inputText id="prontoSocorro" value="#{discenteInfantilMBean.obj.prontoSocorro}" size="80" maxlength="100" />
							<ufrn:help>Em caso de urgência qual pronto socorro procurar?</ufrn:help>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
					<caption>Responsável (1)</caption>
					<tr>
						<th width="15%" class="obrigatorio">CPF:</th>
						<td>
							<h:inputText id="cpfResponsavel" value="#{discenteInfantilMBean.obj.responsavel.pessoa.cpf_cnpj}" size="14" maxlength="14" 
									onkeypress="return formataCPF(this, event, null)">
								<f:converter converterId="convertCpf" />
								<a4j:support event="onblur" action="#{discenteInfantilMBean.carregarDadosResponsavel}" 
									reRender="nomeResponsavel, sexoResponsavel, nascimentoResponsavel, telFixoResponsavelDDD, telefoneResponsavelNumero, 
											celularResponsavelDDD, celularResponsavelNumero, emailResponsavel"/>
							</h:inputText>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Nome:</th>
						<td colspan="3">
							<h:inputText id="nomeResponsavel" value="#{discenteInfantilMBean.obj.responsavel.pessoa.nome}" size="80" maxlength="100"/>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Sexo:</th>
						<td>
							<h:selectOneRadio value="#{discenteInfantilMBean.obj.responsavel.pessoa.sexo}" id="sexoResponsavel" style="border:0px;">
								<f:selectItem itemValue="M" itemLabel="Masculino" />
								<f:selectItem itemValue="F" itemLabel="Feminino" />
							</h:selectOneRadio>
						</td>
						<th class="obrigatorio">Data de Nascimento:</th>
						<td>
							<t:inputCalendar value="#{discenteInfantilMBean.obj.responsavel.pessoa.dataNascimento}" size="10" maxlength="10" onkeypress="return formataData(this,event);"
								id="nascimentoResponsavel" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" >
								<f:converter converterId="convertData"/>
							</t:inputCalendar>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Grau de Parentesco:</th>
						<td colspan="3">
							<h:inputText id="grauParentescoResponsavel" value="#{discenteInfantilMBean.obj.responsavel.grauParentesco}" size="80" maxlength="100"/>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Profissão:</th>
						<td>
							<h:inputText id="profissaoResponsavel" value="#{discenteInfantilMBean.obj.responsavel.profissao}" size="30" maxlength="50"/>
						</td>
						<th class="obrigatorio">Escolaridade:</th>
						<td>
							<h:selectOneMenu id="escolaridadeResponsavel" value="#{discenteInfantilMBean.obj.responsavel.escolaridade.id}">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
								<f:selectItems value="#{discenteInfantilMBean.escolaridadesCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Telefone Fixo:</th>
						<td>
							(<h:inputText onkeyup="return formatarInteiro(this);" value="#{discenteInfantilMBean.obj.responsavel.pessoa.codigoAreaNacionalTelefoneFixo}" maxlength="2" size="1" id="telFixoResponsavelDDD" />) 
							<h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="telefoneResponsavelNumero" value="#{discenteInfantilMBean.obj.responsavel.pessoa.telefone}" maxlength="9" size="9" />
						</td>			
						<th >Celular:</th>
						<td>
							(<h:inputText onkeyup="return formatarInteiro(this);" value="#{discenteInfantilMBean.obj.responsavel.pessoa.codigoAreaNacionalTelefoneCelular}" maxlength="2" size="1" id="celularResponsavelDDD" />) 
							<h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="celularResponsavelNumero" value="#{discenteInfantilMBean.obj.responsavel.pessoa.celular}" maxlength="10" size="10" />
						</td>			
					</tr>
					<tr>
						<th>Telefone do Trabalho:</th>
						<td>
							(<h:inputText onkeyup="return formatarInteiro(this);" value="#{discenteInfantilMBean.obj.responsavel.codigoAreaNacionalTelefoneTrabalho}" maxlength="2" size="1" id="telefoneTrabalhoResponsavelDDD" />) 
							<h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="telefoneTrabalhoResponsavelNumero" value="#{discenteInfantilMBean.obj.responsavel.telefoneTrabalho}" size="9" maxlength="9"/>
						</td>
					</tr>
					<tr>
						<th >E-mail:</th>
						<td colspan="3">
							<h:inputText id="emailResponsavel" value="#{discenteInfantilMBean.obj.responsavel.pessoa.email}" size="60" maxlength="80"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		
		<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
					<caption>Responsável (2)</caption>
					<tr>
						<td width="15%" colspan="4">
							<div class="descricaoOperacao">
								<p>O cadastro do segundo responsável é opcional. Porém, se desejar cadastrá-lo, é necessário informar o CPF.</p>
							</div>
						</td>
					</tr>
					<tr>
						<th width="15%">CPF:</th>
						<td colspan="3">
							<h:inputText id="cpfResponsavelOutro" value="#{discenteInfantilMBean.obj.outroResponsavel.pessoa.cpf_cnpj}" size="14" maxlength="14" onkeypress="return formataCPF(this, event, null)">
								<f:converter converterId="convertCpf" />
								<a4j:support event="onblur" action="#{discenteInfantilMBean.carregarDadosOutroResponsavel}" 
									reRender="nomeResponsavelOutro, sexoResponsavelOutro, nascimentoResponsavelOutro, telFixoResponsavelOutroDDD, telefoneResponsavelOutroNumero, 
											celularResponsavelOutroDDD, celularResponsavelOutroNumero, emailResponsavelOutro"/>
							</h:inputText>
						</td>
					</tr>
					<tr>
						<th >Nome:</th>
						<td colspan="3">
							<h:inputText id="nomeResponsavelOutro" value="#{discenteInfantilMBean.obj.outroResponsavel.pessoa.nome}" size="80" maxlength="100"/>
						</td>
					</tr>
					<tr>
						<th >Sexo:</th>
						<td>
							<h:selectOneRadio value="#{discenteInfantilMBean.obj.outroResponsavel.pessoa.sexo}" id="sexoResponsavelOutro" style="border:0px;">
								<f:selectItem itemValue="M" itemLabel="Masculino" />
								<f:selectItem itemValue="F" itemLabel="Feminino" />
							</h:selectOneRadio>
						</td>
						<th >Data de Nascimento:</th>
						<td>
							<t:inputCalendar value="#{discenteInfantilMBean.obj.outroResponsavel.pessoa.dataNascimento}" size="10" maxlength="10" onkeypress="return formataData(this,event);"
								id="nascimentoResponsavelOutro" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" >
								<f:converter converterId="convertData"/>
							</t:inputCalendar>
						</td>
					</tr>
					<tr>
						<th >Grau de Parentesco:</th>
						<td colspan="3">
							<h:inputText id="grauParentescoResponsavelOutro" value="#{discenteInfantilMBean.obj.outroResponsavel.grauParentesco}" size="50" maxlength="50"/>
						</td>
					</tr>
					<tr>
						<th >Profissão:</th>
						<td>
							<h:inputText id="profissaoResponsavelOutro" value="#{discenteInfantilMBean.obj.outroResponsavel.profissao}" size="30" maxlength="50"/>
						</td>
						<th >Escolaridade:</th>
						<td>
							<h:selectOneMenu id="escolaridadeResponsavelOutro" value="#{discenteInfantilMBean.obj.outroResponsavel.escolaridade.id}">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
								<f:selectItems value="#{discenteInfantilMBean.escolaridadesCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<th >Telefone Fixo:</th>
						<td>
							(<h:inputText onkeyup="return formatarInteiro(this);" value="#{discenteInfantilMBean.obj.outroResponsavel.pessoa.codigoAreaNacionalTelefoneFixo}" maxlength="2" size="1" id="telFixoResponsavelOutroDDD" />) 
							<h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="telefoneResponsavelOutroNumero" value="#{discenteInfantilMBean.obj.outroResponsavel.pessoa.telefone}" maxlength="9" size="9" />
						</td>			
						<th width="25%">Celular:</th>
						<td>
							(<h:inputText onkeyup="return formatarInteiro(this);" value="#{discenteInfantilMBean.obj.outroResponsavel.pessoa.codigoAreaNacionalTelefoneCelular}" maxlength="2" size="1" id="celularResponsavelOutroDDD" />) 
							<h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="celularResponsavelOutroNumero" value="#{discenteInfantilMBean.obj.outroResponsavel.pessoa.celular}" maxlength="10" size="10" />
						</td>
					</tr>
					<tr>
						<th >Telefone do Trabalho:</th>
						<td colspan="3">
							(<h:inputText onkeyup="return formatarInteiro(this);" value="#{discenteInfantilMBean.obj.outroResponsavel.codigoAreaNacionalTelefoneTrabalho}" maxlength="2" size="1" id="telefoneTrabalhoResponsavelOutroDDD" />) 
							<h:inputText onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" id="telefoneTrabalhoResponsavelOutroNumero" value="#{discenteInfantilMBean.obj.outroResponsavel.telefoneTrabalho}" size="9" maxlength="9"/>
						</td>
					</tr>
					<tr>
						<th >E-mail:</th>
						<td colspan="3">
							<h:inputText id="emailResponsavelOutro" value="#{discenteInfantilMBean.obj.outroResponsavel.pessoa.email}" size="60" maxlength="80"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		
		<tr>
			<td colspan="4">
				<table width="100%" class="subFormulario">
					<caption>Outras Informações</caption>
					<tr>
						<th width="10%">Observações:</th>
						<td colspan="3">
							<h:inputTextarea id="resumo" value="#{discenteInfantilMBean.obj.discente.observacao}" rows="10" style="width:99%" 
							readonly="#{projetoBase.readOnly}" onkeydown="limitText(this, countResumo, 1000);" onkeyup="limitText(this, countResumo, 1000);"/>
							<center>
                                 Você pode digitar <input readonly type="text" id="countResumo" size="3" value="${1000 - fn:length(discenteInfantilMBean.obj.discente.observacao) < 0 ? 0 : 1000 - fn:length(discenteInfantilMBean.obj.discente.observacao)}"> caracteres.
                            </center>							
						</td>
					</tr>
				</table>
			</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="4" style="text-align: center">
					<h:commandButton id="btnCadastrar" action="#{discenteInfantilMBean.cadastrar}" value="#{discenteInfantilMBean.confirmButton}"/>
					<h:commandButton id="btnCancelar" action="#{discenteInfantilMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
</h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>

</f:view>

<script type="text/javascript">
	ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro',
			'form:endBairro', 'form:endMunicipio', 'form:ufEnd');
	
	function limitText(limitField, limitCount, limitNum) {
	    if (limitField.value.length > limitNum) {
	        limitField.value = limitField.value.substring(0, limitNum);
	    } else {
	        $(limitCount).value = limitNum - limitField.value.length;
	    }
	}

</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>