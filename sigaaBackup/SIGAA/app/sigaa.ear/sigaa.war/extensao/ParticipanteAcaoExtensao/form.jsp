<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>

<style>
	.aprovada {
		color: #292;		
		font-weight: bold;
	}
	.centralizado {
		text-align: center !important;
	}
</style>

<f:view>
	<a4j:keepAlive beanName="participanteAcaoExtensao"></a4j:keepAlive>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Cadastro de Participantes de Ações de Extensão</h2>

	<div class="descricaoOperacao">Utilize esta tela para registrar os dados do público alvo atingido com a execução da ação.<br />
		Por exemplo: alunos que participarão do curso, pessoas beneficiadas com o projeto, etc.
	</div>

	<h:form id="form">

		<h:inputHidden value="#{participanteAcaoExtensao.confirmButton}" />

		<c:set var="DISCENTE_PARTICIPANTE_EXTENSAO" value="<%= String.valueOf(ParticipanteAcaoExtensao.DISCENTE_UFRN) %>"	scope="application" />
		<c:set var="SERVIDOR_PARTICIPANTE_EXTENSAO" value="<%= String.valueOf(ParticipanteAcaoExtensao.SERVIDOR_UFRN) %>" scope="application" />
		<c:set var="OUTROS_PARTICIPANTE_EXTENSAO" value="<%= String.valueOf(ParticipanteAcaoExtensao.OUTROS) %>" scope="application" />

		<table class="formulario" width="100%">
			<caption class="listagem">Cadastrar Participante</caption>
			<tr>
				<td colspan="2">
				<div>
				<table width="100%">
					<tr>
						<th width="20%" class="rotulo" >Ação de Extensão:</th>
						<td>
							<h:outputText value="#{participanteAcaoExtensao.obj.acaoExtensao.titulo}" id="acao" />
						</td>
					</tr>

					<tr>
						<th width="20%" class="rotulo">Tipo:</th>
						<td>
							<h:outputText value="#{participanteAcaoExtensao.obj.acaoExtensao.tipoAtividadeExtensao.descricao}" id="tipo" />
						</td>
					</tr>
					<c:if test="${participanteAcaoExtensao.obj.acaoExtensao.tipoAtividadeExtensao.id != '2' }">
					<tr>
						<th width="20%" class="rotulo">Carga Horária:</th>
						<td>
							<h:outputText value="#{participanteAcaoExtensao.obj.acaoExtensao.cursoEventoExtensao.cargaHoraria}" id="CH" /> horas
						</td>
					</tr>
					</c:if>
					<tr>
						<th class="required">Tipo de Participante:</th>
						<td><h:selectOneRadio value="#{participanteAcaoExtensao.obj.tipoParticipante}" 
									onclick="javascript:selecionaParticipante(this.value)" id="tipoParticipante"
									disabled="#{participanteAcaoExtensao.confirmButton != 'Cadastrar'}">
								<f:selectItem itemLabel="Discente (UFRN)" itemValue="1" />
								<f:selectItem itemLabel="Servidor(a) (UFRN)" itemValue="2" />
								<f:selectItem itemLabel="Público Externo (Outros)" itemValue="3" />
								<f:selectItem itemLabel="Vários (Discente)" itemValue="4" />
							</h:selectOneRadio>
						</td>
					</tr>

					<tr>
						<th class="required">Categoria:</th>
						<td><h:selectOneMenu id="tipoParticipacao" value="#{participanteAcaoExtensao.obj.tipoParticipacao.id}"
									disabled="#{participanteAcaoExtensao.readOnly}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --" />
								<f:selectItems value="#{participanteAcaoExtensao.tiposParticipacaoCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>

					<tr>
						<th class="required">Frequência:</th>
						<td><h:inputText value="#{participanteAcaoExtensao.obj.frequencia}" readonly="#{participanteAcaoExtensao.readOnly}" 
									id="frequencia" maxlength="3" size="4" onkeyup="return formatarInteiro(this)" /> %</td>
					</tr>

					<tr>
						<th>Autorizar Certificado? 
							<ufrn:help img="/img/ajuda.gif">Autoriza a receber certificado de participação da Ação de Extensão</ufrn:help>
						</th>
						<td><h:selectOneRadio value="#{participanteAcaoExtensao.obj.autorizacaoCertificado}"
									disabled="#{participanteAcaoExtensao.readOnly}" id="certificado">
								<f:selectItem itemValue="true" itemLabel="SIM" />
								<f:selectItem itemValue="false" itemLabel="NÃO" />
							</h:selectOneRadio>
						</td>
					</tr>

					<tr>
						<th>Autorizar Declaração? 
							<ufrn:help img="/img/ajuda.gif">Autoriza a receber declaração de participação da Ação de Extensão</ufrn:help>
						</th>
						<td><h:selectOneRadio value="#{participanteAcaoExtensao.obj.autorizacaoDeclaracao}"
									disabled="#{participanteAcaoExtensao.readOnly}" id="declaracao">
								<f:selectItem itemValue="true" itemLabel="SIM" />
								<f:selectItem itemValue="false" itemLabel="NÃO" />
							</h:selectOneRadio>
						</td>
					</tr>

				</table>
				</div>
				</td>
			</tr>

			<tr>
				<td colspan="2">

				<div id='discente' style="display: none">
				<table width="100%">
					<tr>
						<th width="20%" class="required">Discente:</th>
						<td>
							<c:if test="${((!participanteAcaoExtensao.readOnly) 
									and (participanteAcaoExtensao.obj.tipoParticipante == DISCENTE_PARTICIPANTE_EXTENSAO)) 
									or (participanteAcaoExtensao.confirmButton == 'Cadastrar')}">
								<h:inputHidden id="idDiscente" value="#{participanteAcaoExtensao.obj.discente.id}"></h:inputHidden>
								<h:inputText id="nomeDiscente" value="#{participanteAcaoExtensao.obj.discente.pessoa.nome}"	size="70" />

								<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente" baseUrl="/sigaa/ajaxDiscente"
										className="autocomplete" indicator="indicatorDiscente" minimumCharacters="3" 
										parameters="nivel=ufrn"	parser="new ResponseXmlToHtmlListParser()" />
								<span id="indicatorDiscente" style="display: none;"> <img src="/sigaa/img/indicator.gif" /> </span>
								<ufrn:help img="/img/ajuda.gif">Apenas os Discentes Ativos da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
							</c:if> 
							
							<c:if test="${participanteAcaoExtensao.readOnly}">
								<h:inputText value="#{participanteAcaoExtensao.obj.discente.pessoa.nome}"
										readonly="#{participanteAcaoExtensao.readOnly}" size="40" />
							</c:if>
						</td>
					</tr>
				</table>
				</div>

				<div id='servidor' style="display: none">
				<table width="100%">
					<tr>
						<th width="20%" class="required">Servidor(a):</th>
						<td>
							<c:if test="${(!participanteAcaoExtensao.readOnly) 
									and (participanteAcaoExtensao.obj.tipoParticipante == SERVIDOR_PARTICIPANTE_EXTENSAO) 
									or (participanteAcaoExtensao.confirmButton == 'Cadastrar')}">
								<h:inputHidden id="idServidor" value="#{participanteAcaoExtensao.obj.servidor.id}"></h:inputHidden>
								<h:inputText id="nomeServidor" value="#{participanteAcaoExtensao.obj.servidor.pessoa.nome}"	size="70" />

								<ajax:autocomplete source="form:nomeServidor" target="form:idServidor" baseUrl="/sigaa/ajaxServidor"
										className="autocomplete" indicator="indicator" minimumCharacters="3" 
										parameters="tipo=todos,situacao=ativo" parser="new ResponseXmlToHtmlListParser()" />
								<span id="indicator" style="display: none;"> 
									<img src="/sigaa/img/indicator.gif" /> 
								</span>
								<ufrn:help img="/img/ajuda.gif">Apenas os servidores do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
							</c:if> 
							
							<c:if test="${participanteAcaoExtensao.readOnly}">
								<h:inputText value="#{participanteAcaoExtensao.obj.servidor.pessoa.nome}" id="nomeDoServidor"
										readonly="#{participanteAcaoExtensao.readOnly}" size="40" />
							</c:if>
						</td>
					</tr>
				</table>
				</div>
				<div id='outros'>
				
				<table width="100%" >
					
					<tr>	
						<th width="20%">Estrangeiro: </th>
						<td>
							<h:selectBooleanCheckbox id="checkEstrangeiro" value="#{participanteAcaoExtensao.obj.internacional}" >
								<a4j:support id="suportCheck" event="onclick" reRender="form"/>
							</h:selectBooleanCheckbox>
						</td>
					</tr>
					<c:choose>
					<c:when test="${participanteAcaoExtensao.obj.internacional}">
					<tr>
						<th class="required" width="20%">
							<h:outputText value="Passaporte:" />
						</th>
						<td>
							<h:inputText value="#{participanteAcaoExtensao.obj.passaporte}" maxlength="20" size="20" id="passaporte" />
							<ufrn:help img="/img/ajuda.gif">Passaporte é obrigatório para os participantes estrangeiros que 
								recebem certificado e/ou declaração.</ufrn:help>
					 	</td>
						
					</tr>
					</c:when>
					<c:when test="${!participanteAcaoExtensao.obj.internacional}">
					<tr>
						<th class="required" width="20%">
							<h:outputText value="CPF:" />
						</th>
						<td>						
							<h:inputText value="#{participanteAcaoExtensao.obj.cpf}" readonly="#{participanteAcaoExtensao.readOnly}" 
									size="14" id="cpf" maxlength="14" onkeypress="formataCPF(this, event, null)" >
									<f:converter converterId="convertCpf" />
									<f:param id="paraCpf" name ="type" value="cpf" />
							</h:inputText>
							<ufrn:help img="/img/ajuda.gif">CPF é obrigatório para os participantes que 
								recebem certificado e/ou declaração.</ufrn:help>
						</td>
					</tr>
					</c:when>
					</c:choose>				
					<tr>
						<th class="required">Nome:</th>
						<td><h:inputText value="#{participanteAcaoExtensao.obj.nome}" id="nome"
									readonly="#{participanteAcaoExtensao.readOnly}" size="70" /></td>
					</tr>

					<tr>
						<th>Data Nascimento:</th>
						<td><t:inputCalendar value="#{participanteAcaoExtensao.obj.dataNascimento}" maxlength="10"
									size="10" disabled="#{participanteAcaoExtensao.readOnly}" renderAsPopup="true" 
									renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))"  
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="dataNascimento" />
						</td>
					</tr>

					<tr>
						<th>E-mail:</th>
						<td><h:inputText value="#{participanteAcaoExtensao.obj.email}" id="email"
									readonly="#{participanteAcaoExtensao.readOnly}" size="60" maxlength="60" /></td>
					</tr>

							<tr>
								<th>Instituição:</th>
								<td><h:inputText
										value="#{participanteAcaoExtensao.obj.instituicao}"
										id="instituicao"
										readonly="#{participanteAcaoExtensao.readOnly}"
										style="width:95%" maxlength="100" /></td>
							</tr>
							<tr>
								<th>CEP:</th>
								<td><h:inputText
										value="#{participanteAcaoExtensao.obj.cep}"
										readonly="#{participanteAcaoExtensao.readOnly}" size="10"
										id="cep" maxlength="10"
										onkeyup="formataCEP(this, event, null)">
									</h:inputText></td>
							</tr>
							<tr>
								<th>Endereço:</th>
								<td><h:inputText
										value="#{participanteAcaoExtensao.obj.endereco}" id="endereco"
										readonly="#{participanteAcaoExtensao.readOnly}"
										style="width: 95%;" maxlength="100" /></td>
							</tr>


							<tr>
				<th class="required">UF:</th>
				<td><h:selectOneMenu value="#{participanteAcaoExtensao.obj.unidadeFederativa.id}" id="uf" 
							readonly="#{participanteAcaoExtensao.readOnly}" immediate="true">
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
						<a4j:support event="onchange" reRender="municipio" action="#{participanteAcaoExtensao.carregarMunicipios}"/>
					</h:selectOneMenu>
				</td>
				<th class="required">Município:</th>
				<td>
					<h:selectOneMenu value="#{participanteAcaoExtensao.obj.municipio.id}" id="municipio" 
							readonly="#{participanteAcaoExtensao.readOnly}">
					<f:selectItems value="#{participanteAcaoExtensao.municipiosEndereco}" />
					</h:selectOneMenu>
				</td>
			</tr>
				</table>
				</div>
				<div id='discentes' style="display: none">
					<table width="100%">
						<tr>
							<c:if test="${((!participanteAcaoExtensao.readOnly) 
									and (participanteAcaoExtensao.obj.tipoParticipante == DISCENTE_PARTICIPANTE_EXTENSAO)) 
									or (participanteAcaoExtensao.confirmButton == 'Cadastrar')}">
								<th class="required" width="20%">Matrículas:</th>
								<td><h:inputTextarea value="#{participanteAcaoExtensao.matriculas}"	rows="3" 
											id="matriculas" style="width: 95%;" />
									<ufrn:help>O cadastro coletivo de participantes (Discente) é realizado através da informação das 
										respectivas matrículas dos alunos, separados por vírgula, espaço em branco ou quebra de linha. 
										As matrículas devem ser informadas apenas com números.</ufrn:help>
								</td>
							</c:if>
						</tr>
					</table>
				</div>
	
		<table width="100%">
		 <tr><th width="20%">Observação no Certificado:</th>		 
			<td><h:inputTextarea id="observacoCertificado" label="Observação no Certificado" value="#{participanteAcaoExtensao.obj.observacaoCertificado}" rows="2" cols="60"  style="width: 95%;"
				onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 180);">
			</h:inputTextarea>			
			<br/>
			Caracteres Restantes: <span id="quantidadeCaracteresDigitados">180</span>
					
			
			</td>
		</tr>				
		</table>

				</td>
			</tr>
			
			
		
			<tfoot>
				<tr>
					<td colspan="2">
						<c:if test="${participanteAcaoExtensao.continuarCadastroRelatorio}">
							<input type="hidden" name="idRelatorio" value="${relatorioAcaoExtensao.obj.id}" />
							<h:commandButton value="<< Retornar ao Relatório" id="btcontinuar_relatorio" 
									action="#{relatorioAcaoExtensao.preAlterarRelatorio}" 
									rendered="#{participanteAcaoExtensao.continuarCadastroRelatorio}" />
  						 	&nbsp;&nbsp;&nbsp;&nbsp;
						</c:if>
										
						<h:commandButton value="#{participanteAcaoExtensao.confirmButton}" action="#{participanteAcaoExtensao.cadastrar}" 
								id="btcadastrar" rendered="#{participanteAcaoExtensao.confirmButton != 'Remover'}" />
						<h:commandButton value="<< Voltar" action="#{participanteAcaoExtensao.listarAcoesExtensao}" 
								id="btVoltar" rendered="#{participanteAcaoExtensao.confirmButton != 'Remover'}" /> 
						<h:commandButton value="#{participanteAcaoExtensao.confirmButton}" action="#{participanteAcaoExtensao.remover}" 
								id="btRemover" rendered="#{participanteAcaoExtensao.confirmButton == 'Remover'}" /> 
						<h:commandButton value="Cancelar" id="btcancelar" action="#{participanteAcaoExtensao.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>

		</table>
		<br />
		<center>
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</center>
		<br />
	</h:form>

 	

	<script type="text/javascript">

		function selecionaParticipante(obj) {

			var servidor = document.getElementById("servidor") ;
			var discente = document.getElementById("discente") ;
			var outros = document.getElementById("outros") ;
			var discentes = $('discentes');

			if (obj == 1){
				servidor.style.display = "none";
				discente.style.display = "";
				outros.style.display = "none";
				discentes.style.display = "none";
			}
			if (obj == 2){
				servidor.style.display = "";
				discente.style.display = "none";
				outros.style.display = "none";
				discentes.style.display = "none";
			}
			if (obj == 3){
				servidor.style.display = "none";
				discente.style.display = "none";
				outros.style.display = "";
				discentes.style.display = "none";
			}
			if (obj == 4){
				servidor.style.display = "none";
				discente.style.display = "none";
				outros.style.display = "none";
				discentes.style.display = "";
			}
		}
		
		selecionaParticipante('${sessionScope.participanteAcaoExtensao.obj.tipoParticipante}');
		
		
		function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
			
			if (field.value.length > maxlimit){
				field.value = field.value.substring(0, maxlimit);
			}else{ 
				document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
			} 
		}
		
	</script>

</f:view>

<script type="text/javascript">
	var posProcessamento = function() {
		$('form:municipio').value = $('form:municipio').options[0].value;
		$('form:uf').onchange();
	}
	ConsultadorCep.init('/sigaa/consultaCep','form:cep','form:endereco','form:bairro','form:municipio','form:uf',posProcessamento);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<script type="text/javascript">
function checkAll(elem) {
    $A(document.getElementsByClassName('check')).each(function(e) {
        e.checked = elem.checked;
    });
}
</script>
