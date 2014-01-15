<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
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
<script type="text/javascript">

// seta UF escolhida no Hidden pro servlet poder receber o parametro
function preNaturUF() {
	$('naturUF').value = $F('formDiscente:uf');
}

// seleciona o Municipio ao carregar o form pra alteração
function posNaturUF() {
	<c:if test="${not empty naturMunicipio}">
	var items = $A($('formDiscente:municipio').options);
	items.each(
		function(item) {
			if (item.value == ${naturMunicipio}) {
				item.selected = true;
			}
		}
	);
	</c:if>
}
</script>

<style>
table.formulario th {
	font-weight: bold;
}

#cepIndicator {
	padding: 0 0 0 15px;
	color: #999;
}

span.info {
	font-size: 0.9em;
	color: #888;
}
</style>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp"%>
	<%@include file="/stricto/menu_coordenador.jsp"%>

	<h2> <ufrn:subSistema /> &gt; Atualizar Dados Pessoais de Discente</h2>

	<c:set var="required" value="class = '#{alteracaoDadosDiscente.acessoDiscente ? 'required' : '' }'"/>

	<c:if test="${alteracaoDadosDiscente.acessoDiscente}">
	<div class="descricaoOperacao">
		<p> 
			<b>Caro Discente,</b> 
		</p>
		<br/> 
		<p>
			No caso de algum dos dados pessoais bloqueados para edição estiver incorreto, será necessário procurar a coordenação do seu curso para corrigí-los.
		</p>
	<p><b>É importante</b> que o seu nome esteja grafado corretamente e
	sem abreviações, assim como o nome do seu pai e de sua mãe. Verifique
	também a corretude na naturalidade e documentos, principalmente <b>CPF</b> e <b>RG</b>. <b>Tais informações
	constarão no seu diploma de graduação</b>. </p>
	</div>
	</c:if>	
	<h:form id="formDiscente">
		
		<h:inputHidden value="#{alteracaoDadosDiscente.obj.id}" />
		<c:set var="discenteVar" value="#{alteracaoDadosDiscente.obj}" />
		<table class="formulario">
			<caption>Dados do Discente</caption>
			<tbody>
				<tr>
					<th width="20%">Matrícula:</th>
					<td colspan="3">${discenteVar.matricula}</td>
				</tr>
				<tr>
					<th>Nome:</th>
					<td colspan="3">${discenteVar.pessoa.nome}</td>
				</tr>
				<tr>
					<th>Curso:</th>
					<td colspan="3">${discenteVar.curso.descricao}</td>
				</tr>
				<tr>
					<th>Sexo:</th>
					<td>${discenteVar.pessoa.sexo}</td>
					<th width="25%">Estado Civil:</th>
					<td>${discenteVar.pessoa.estadoCivil.descricao}</td>
				</tr>
				<tr>
					<th>Data de Nascimento:</th>
					<td><fmt:formatDate pattern="dd/MM/yyyy"
						value="${discenteVar.pessoa.dataNascimento}" /></td>
					<th>Naturalidade:</th>
					<c:set var="municipioNaturalidade" value="${discenteVar.pessoa.municipio.nome} /  ${discenteVar.pessoa.unidadeFederativa.descricao}" />
					<td>${discenteVar.pessoa.municipio.nome eq null 
						? discenteVar.pessoa.municipioNaturalidadeOutro 
						: municipioNaturalidade } 
					</td>
				</tr>
				<tr>
					<th>Raça:</th>
					<td>${discenteVar.pessoa.tipoRaca.descricao}</td>
					<th> Tipo Sanguíneo:</th>
					<td>${discenteVar.pessoa.tipoSanguineo}</td>
				</tr>
				<tr>
					<th>Nacionalidade:</th>
					<td> ${discenteVar.pessoa.paisNacionalidade eq null ? discenteVar.pessoa.pais.nacionalidade : discenteVar.pessoa.paisNacionalidade} </td>
					<th>País:</th>
					<td>${discenteVar.pessoa.pais.nome}</td>
				</tr>
				<tr>
					<th>Escola de Conclusão do Ensino Médio:</th>
					<td>${discenteVar.pessoa.escolaConclusaoEnsinoMedio}</td>
					<th>Ano de Conclusão:</th>
					<td>${discenteVar.pessoa.anoConclusaoEnsinoMedio}</td>
				</tr>
				<tr>
					<th>Tipo de Necessidade Especial:</th>
					<td colspan="3">${discenteVar.pessoa.tipoNecessidadeEspecial.descricao}</td>
				</tr>
				<tr>
					<th>Nome do Pai:</th>
					<td colspan="3">${discenteVar.pessoa.nomePai}</td>
				</tr>
				<tr>
					<th>Nome da Mãe:</th>
					<td colspan="3">${discenteVar.pessoa.nomeMae}</td>
				</tr>

				<%-- DOCUMENTOS  --%>
				<tr>
					<td colspan="4" class="subFormulario">Documentos</td>
				</tr>
				<tr>
					<th>CPF:</th>
					<td colspan="3"><ufrn:format type="cpf_cnpj"
						name="alteracaoDadosDiscente" property="obj.pessoa.cpf_cnpj" /></td>
				</tr>
				<tr>
					<th>RG:</th>
					<td>${discenteVar.pessoa.identidade.numero} -
					${discenteVar.pessoa.identidade.orgaoExpedicao}/${discenteVar.pessoa.identidade.unidadeFederativa.sigla}</td>
					<th>Data de Expedição:</th>
					<td><fmt:formatDate
						value="${ discenteVar.pessoa.identidade.dataExpedicao }"
						pattern="dd/MM/yyyy" /></td>
					
					<%-- 
					<ufrn:format name="discente"
						property="pessoa.identidade.dataExpedicao" type="data" /></td>
					--%>
				</tr>
				<tr>
					<th>Título de Eleitor:</th>
					<td>${ discenteVar.pessoa.tituloEleitor.numero } &nbsp; <b>Zona:</b>
					${ discenteVar.pessoa.tituloEleitor.zona }</td>
					<th>Seção:</th>
					<td>${discenteVar.pessoa.tituloEleitor.secao} &nbsp; <b>UF:</b>
					${discenteVar.pessoa.tituloEleitor.unidadeFederativa.sigla}</td>
				</tr>
				<tr>
					<th>Cert. Militar:</th>
					<td>${ discenteVar.pessoa.certificadoMilitar.numero }</td>
					<th>Data de Expedição do Certificado Militar:</th>
					<td><fmt:formatDate
						value="${ discenteVar.pessoa.certificadoMilitar.dataExpedicao }"
						pattern="dd/MM/yyyy" /></td>
				</tr>
				<tr>
					<th>Série:</th>
					<td>${ discenteVar.pessoa.certificadoMilitar.serie }</td>
					<th>Categoria:</th>
					<td>${ discenteVar.pessoa.certificadoMilitar.categoria } &nbsp; <b> Órgão:</b>
					${ discenteVar.pessoa.certificadoMilitar.orgaoExpedicao }</td>
				</tr>


				<tr>
					<td colspan="4" class="subFormulario">Endereço</td>
				</tr>
				<tr class="linhaCep">
					<th>CEP:</th>
					<td colspan="3">
						<h:inputText value="#{alteracaoDadosDiscente.obj.pessoa.enderecoContato.cep}" maxlength="10" size="10" disabled="#{dadosPessoais.readOnly}" 
							onkeyup="return formataCEP(this, event, null);"	id="endCEP"	onblur="formataCEP(this, event, null); ConsultadorCep.consultar();" />
						<a href="javascript://nop/" onclick="ConsultadorCep.consultar();">
							<img src="/sigaa/img/buscar.gif" />
						</a>
						<span class="info">
							(clique na lupa para buscar o endereço do CEP informado)
						</span>
						<span id="cepIndicator" style="display: none;">
							<img src="/sigaa/img/indicator.gif" /> Buscando endereço... 
						</span>
					</td>
				</tr>
				<tr>
					<th ${required}>Logradouro:</th>
					<td colspan="3">
						<h:selectOneMenu value="#{alteracaoDadosDiscente.obj.pessoa.enderecoContato.tipoLogradouro.id}"
							style="width: 20%;" id="selectTipoLogradouro">
							<f:selectItems value="#{tipoLogradouro.allCombo}" />
						</h:selectOneMenu> &nbsp;
						<h:inputText value="#{alteracaoDadosDiscente.obj.pessoa.enderecoContato.logradouro}"
						 	style="width: 75%;" id="txtLogradouro" maxlength="120" />	
					</td>
				</tr>
				<tr>
					<th ${required}>Número:</th>
					<td><h:inputText
						value="#{alteracaoDadosDiscente.obj.pessoa.enderecoContato.numero}"
						size="10" id="txtNumero" maxlength="30" /></td>
					<th>Complemento:</th>
					<td>
						<h:inputText value="#{alteracaoDadosDiscente.obj.pessoa.enderecoContato.complemento}"
						 maxlength="500" id="txtComplemento" size="50"/>
					</td>
				</tr>
				<tr>
					<th ${required}>Bairro:</th>
					<td colspan="3"><h:inputText
						value="#{alteracaoDadosDiscente.obj.pessoa.enderecoContato.bairro}"
						maxlength="80" size="50" id="txtBairro" /></td>
				</tr>
				<tr>
					<th ${required}>UF:</th>
					<td><a4j:region>
						<h:selectOneMenu
							value="#{alteracaoDadosDiscente.obj.pessoa.enderecoContato.unidadeFederativa.id}"
							immediate="true" id="selectUF">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
							<a4j:support event="onchange"
								action="#{alteracaoDadosDiscente.carregarMunicipiosEndereco}"
								reRender="selectMunicipio" />
						</h:selectOneMenu>
						<input type="hidden" id="naturUF" />
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region></td>
					<th ${required}>Município:</th>
					<td><h:selectOneMenu
						value="#{alteracaoDadosDiscente.obj.pessoa.enderecoContato.municipio.id}"
						id="selectMunicipio">
						<f:selectItems value="#{alteracaoDadosDiscente.municipiosEndereco}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td colspan="4" class="subFormulario">Contatos</td>
				</tr>
				<tr>
					<th>Telefone:</th>
					<td>
						(<h:inputText value="#{alteracaoDadosDiscente.obj.pessoa.codigoAreaNacionalTelefoneFixo}"
						maxlength="2" size="2" id="telFixoDDD" onkeyup="return formatarInteiro(this);"  onblur="return formatarInteiro(this);"/>)
						<h:inputText
						value="#{ alteracaoDadosDiscente.obj.pessoa.telefone }"
						onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" 
						maxlength="9" size="9" id="txtTelefone"  /></td>
					<th>Celular:</th>
					<td>
						(<h:inputText  onkeyup="return formatarInteiro(this);"  onblur="return formatarInteiro(this);"
					    value="#{alteracaoDadosDiscente.obj.pessoa.codigoAreaNacionalTelefoneCelular}" maxlength="2" size="2" id="telCelDDD" />)
						<h:inputText
						value="#{ alteracaoDadosDiscente.obj.pessoa.celular }"
						onkeypress="mascara( this, mtel );" onblur="mascara( this, mtel );" 
						maxlength="10" size="10" id="txtCelular" /></td>
				</tr>
				<tr>
					<th ${required}>E-Mail:</th>
					<td colspan="3"><h:inputText
						value="#{ alteracaoDadosDiscente.obj.pessoa.email }" 
						size="50" maxlength="80" id="txtEmail" /></td>
				</tr>

				<tr>
					<td colspan="4" class="subFormulario">Dados Bancários</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="descricaoOperacao">
							<p>
								Não é permitido informar dados bancários de terceiros. Apenas uma conta bancária que tenha como titular o próprio aluno,
								 será aceita no cadastro para o recebimento de qualquer tipo de auxílio financeiro ou bolsa remunerada que o mesmo possa 
								 vir a ter na Universidade.
							</p>
						</div> 
					</td>
				</tr>
				<tr>
					<th> Banco:</th>
					<td>
						<h:selectOneMenu id="selectBanco" value="#{alteracaoDadosDiscente.obj.pessoa.contaBancaria.banco.id}" onchange="exibeDadosBancarios(this)">
							<f:selectItem itemValue="-1" itemLabel="-- SELECIONE UM BANCO --"  />
							<f:selectItems value="#{banco.allCombo}"/>
						</h:selectOneMenu>
					</td>
					<th> Nº Operação: </th>
					<td> <h:inputText maxlength="20" id="operacao" value="#{alteracaoDadosDiscente.obj.pessoa.contaBancaria.operacao}" /> </td>
				</tr>
				<tr id="agenciaConta">
					<th> Nº Agência: </th>
					<td> <h:inputText maxlength="20" id="agencia" value="#{alteracaoDadosDiscente.obj.pessoa.contaBancaria.agencia}" />	</td>
					<th> Nº Conta Corrente: </th>
					<td> <h:inputText maxlength="20" id="contacorrente" value="#{alteracaoDadosDiscente.obj.pessoa.contaBancaria.numero}" /> </td>
				</tr>
				
				<c:if test="${alteracaoDadosDiscente.acessoDiscente}">
				<tr>
					<td colspan="4" class="subFormulario">Situação Sócio-Econômica</td>
				</tr>
				<tr>
					<td colspan="4">
						<div class="descricaoOperacao">
							<p>
								De acordo com a RESOLUÇÃO No 169/2008-CONSEPE, de 02 de dezembro de
								2008, a distribuição de bolsas da ${ configSistema['siglaInstituicao'] } será prioritária para alunos
								que se enquadrem na condição sócio-econômica carente. Diante disso,
								é necessário que você informe sua renda familiar para registro no
								sistema.
							</p>
							<p>
								Alertamos que o lançamento de dados falsos, constatados em
								ato da comprovação a ser realizado pela Secretaria de Assuntos
								Estudantis - SAE, poderá implicar em restrições administrativas à
								concessão de bolsa.							
							</p>
						</div> 
					</td>
				</tr>
				<tr>
					<th ${required} alt="Renda Familiar (mensal)">Renda Familiar (mensal):</th>
					<td valign="top"> R$ 
						<h:inputText value="#{ alteracaoDadosDiscente.situacaoSocioEconomica.rendaFamiliar }" 
							size="10" id="txtRendaFamiliar" onfocus="javascript:select()" onkeypress="return(formataValor(this, event, 2))" style="text-align: right;">
							<f:converter converterId="convertMoeda"/> 
						</h:inputText>
					</td>
					<th ${required}>Quantidade de membros do grupo familiar:</th>
					<td valign="top">
						<h:inputText value="#{ alteracaoDadosDiscente.situacaoSocioEconomica.quantidadeMembrosFamilia }" 
							size="5" maxlength="3" id="txtQuantidadeMembros" onkeyup="return formatarInteiro(this);" style="text-align: right;">
						</h:inputText>
					</td>
				</tr>
				</c:if>
				<tr>
					<td colspan="4" class="subFormulario">Confirmação de Senha</td>
				</tr>
				<tr>
					<td colspan="4">
						<div align="center">
							<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
						</div>
					</td>	
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="#{ alteracaoDadosDiscente.confirmButton }" action="#{ alteracaoDadosDiscente.atualizarDados }" id="btnConfirmar" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ alteracaoDadosDiscente.cancelar }" id="btnCancelar" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<c:if test="${alteracaoDadosDiscente.acessoDiscente}">
		<br>
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
			class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
		<br>
		</center>
		</c:if>
	</h:form>
</f:view>

<script type="text/javascript">

		var posProcessamento = function() {
			$('formDiscente:selectUF').onchange();
		};

	ConsultadorCep.init('/sigaa/consultaCep', 'formDiscente:endCEP', 'formDiscente:txtLogradouro', 'formDiscente:txtBairro', 'formDiscente:selectMunicipio', 'formDiscente:selectUF', posProcessamento );

	function exibeDadosBancarios(sel) {
		var val = sel.options[sel.selectedIndex].value;
		if (val > 0) {
			$('agenciaConta').show();
		} else {
			$('agenciaConta').hide();
		}
	}
	exibeDadosBancarios($('formDiscente:selectBanco'));
	
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>