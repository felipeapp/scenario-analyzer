<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>

<%-- Java script para ativar o campo onde o usu�rio informa a quantiade de dias do empr�stimo personalizado --%>
<script type="text/javascript">		

    /* Fun��o que cont�m a l�gica para mostrar e ocutar a quantidade de dias para os empr�stimos personalizados
     * sempre chamada quando a p�gina � garregada, porque sen�o, o campo dias era ocultado sempre que a p�gina era recarregada
     */
	function ativaDias (idselect){

		var valor = document.getElementById(idselect).value;

		if (valor == undefined)
			valor = select.val();
		
		// se a linha para escolher o tipo de empr�timo est� sendo mostrada e o tipo de empr�stimo escolhido 
		// � personaliz�vel, mostra a quantidade de dias
		if (valor == <h:outputText value="#{emprestimoInstitucionalMBean.idTipoEmprestimoPersonalizado}"/>  ){
			document.getElementById('linhaDiasAEmprestarTH').style.display = '';
			document.getElementById('linhaDiasAEmprestarTD').style.display = '';
			document.getElementById('senhaOperador').style.display = 'block';
			document.getElementById('qtdDiasEmprestimo').focus();
		} else {
			document.getElementById('linhaDiasAEmprestarTH').style.display = 'none';
			document.getElementById('linhaDiasAEmprestarTD').style.display = 'none';
			document.getElementById('senhaOperador').style.display = 'none';	
		}
	}
    
	window.onload = function() {
		ativaDias('formEmprestimosInstitucionais:tipoEmprestimoInstitucional');
	};
	
	
	//fun��o que executa o click no botao passado quando o usu�rio pressiona o enter
	function executaClickBotao(evento, idBotao) {
		
		var tecla = "";
		if (isIe())
			tecla = evento.keyCode;
		else
			tecla = evento.which;

		if (tecla == 13){
			document.getElementById(idBotao).click();
			return false;
		}
		
		return true;
		
	}	

	// testa se � o IE ou n�o
	function isIe() {
		return (typeof window.ActiveXObject != 'undefined');
	}
	
    
</script>    

	<h2><ufrn:subSistema /> &gt; Realizar Empr�stimo Institucional &gt; ${emprestimoInstitucionalMBean.tipoBiblioteca }</h2>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<div class="descricaoOperacao" style="width: 85%">
		<p>Utilize este formul�rio para realizar os empr�stimos de materiais a outras Bibliotecas ou Unidades</p>
	</div>

	<a4j:keepAlive beanName="emprestimoInstitucionalMBean"/>

	<h:form id="formEmprestimosInstitucionais">

		<%-- Para submeter o par�metro caso seja um empr�stimo institucional, j� que com ajax n�o atualiza esse campo  --%>
		<c:set var="exibirApenasSenha" value="true" scope="request"/>


		<div class="infoAltRem" style="width: 85%;">
			<img src="${ctx}/img/delete.gif">: Remover Material
		</div>

		<table class="formulario" width="95%">
			<caption>Realizar Empr�stimo Institucional</caption>

			<tr>
				<c:if test="${not emprestimoInstitucionalMBean.emprestimoParaBibliotecaExterna}">
					<th class="obrigatorio">Biblioteca Interna:</th>
					<td>
						<h:selectOneMenu
							value="#{emprestimoInstitucionalMBean.biblioteca.id}">
							<f:selectItem itemLabel="-- Escolha --" itemValue="0" />
							<f:selectItems value="#{emprestimoInstitucionalMBean.bibliotecasInternas}" />
							
							<a4j:support event="onchange" actionListener="#{emprestimoInstitucionalMBean.atualizaBibliotecaEmprestimo}" reRender="formEmprestimosInstitucionais"></a4j:support>
						</h:selectOneMenu>
					</td>
				</c:if>

				<c:if
					test="${emprestimoInstitucionalMBean.emprestimoParaBibliotecaExterna}">
					<th class="obrigatorio">Biblioteca Externa:</th>
					<td>
						<h:selectOneMenu
							value="#{emprestimoInstitucionalMBean.biblioteca.id}">
							<f:selectItem itemLabel="-- Escolha --" itemValue="0" />
							<f:selectItems value="#{emprestimoInstitucionalMBean.bibliotecasExternas}" />
							
							<a4j:support event="onchange" actionListener="#{emprestimoInstitucionalMBean.atualizaBibliotecaEmprestimo}" reRender="formEmprestimosInstitucionais"></a4j:support>
						</h:selectOneMenu>
					</td>
				</c:if>
			</tr>

			<tr>
				<td colspan="3">
				<%-- Exibe as informa��es do usu�rio. --%>
				<c:set var="_infoUsuarioCirculacao" value="${emprestimoInstitucionalMBean.infoUsuario}" scope="request" />
				<c:set var="_situacoesUsuario" value="${emprestimoInstitucionalMBean.situacoesUsuario}" scope="request" />
				<c:set var="_mostrarFoto" value="false" scope="request" />
				 
				<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
				</td>
			</tr>

			<tr>
				<td colspan="2">
				<table width="100%" class="subFormulario">
					<caption>Adicionar Material</caption>

					
					<tr>
						<td style="width: 30%">C�digo de Barras: 
						
							<h:inputText id="campoCB" value="#{emprestimoInstitucionalMBean.codigoBarras}" onkeypress="return executaClickBotao(event, 'formEmprestimosInstitucionais:botaoAdicionarMaterial' )"  />
						</td>
						<td style="text-align: left;">	
							<h:commandButton id="botaoAdicionarMaterial" value="Buscar Material" title="Buscar Material"  style="margin-left:10px;" actionListener="#{emprestimoInstitucionalMBean.buscarMaterialByCodigoBarras}"/>
						</td>
						<td style="width: 20%">
						</td>
					</tr>
					
					
					<c:if test="${fn:length(emprestimoInstitucionalMBean.materiais) <= 0}">
						<tr>
							<td colspan="3">
								<div style="color:#FF0000;text-align:center;">N�o h� materiais cadastrados para o empr�stimo.</div>
							</td>
						</tr>
					</c:if>
					
					<c:if test="${ fn:length( emprestimoInstitucionalMBean.materiais ) > 0}">
						<tr>
							<td style="width: 30%">Tipo do Empr�stimo:</td>
							<td id="linhaTipoEmprestimo"  style="text-align: left;" >
								<h:selectOneMenu id="tipoEmprestimoInstitucional" value="#{emprestimoInstitucionalMBean.tipoEmprestimoEscolhido.id}"
										onchange="ativaDias('formEmprestimosInstitucionais:tipoEmprestimoInstitucional'); " onkeyup="ativaDias('formEmprestimosInstitucionais:tipoEmprestimoInstitucional');">
									<f:selectItem itemValue="-1" itemLabel=" -- SELECIONE -- " />
									<f:selectItems value="#{emprestimoInstitucionalMBean.tiposEmprestimosComboBox}" />
								</h:selectOneMenu>
							</td>
							<td style="width: 20%">
							</td>
						</tr>
					
						<tr>
							<th id="linhaDiasAEmprestarTH" style="display:none;" class="obrigatorio">Prazo do empr�stimo:</th>
							<td id="linhaDiasAEmprestarTD" style="display:none;">
								<h:inputText id="qtdDiasEmprestimo" size="3" maxlength="3" autocomplete="off" value="#{emprestimoInstitucionalMBean.diasAEmprestar}"
									onkeyup="return formatarInteiro(this);" /> dia(s)
								<ufrn:help>A quantidade de dias que o usu�rio vai poder ficar com o material.</ufrn:help>
							</td>
						</tr>
					
					
						<tr>
							<td colspan="3">
								<div id="senhaOperador" style="display:none;">
									<c:set var="exibirApenasSenha" value="true" scope="request"/>
									<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
								</div>
							</td>
						</tr>
					
					
					
						<tr>
							<td colspan="3">
								
								<t:dataTable var="m" columnClasses="codigobarras, acao" value="#{emprestimoInstitucionalMBean.dmMateriais}" id="materiais" width="100%" rowClasses="linhaPar,linhaImpar">

									<t:column>
										<f:facet name="header">
											<h:outputText value="Materiais a serem Emprestados" />
										</f:facet>
										<h:outputText value="#{m.informacao}" />
									</t:column>

									<t:column width="20">
										<f:facet name="header">
											<h:outputText value="" />
										</f:facet>
										<a4j:commandLink title="Remover"
											actionListener="#{emprestimoInstitucionalMBean.removerMaterial}"
											reRender="formEmprestimosInstitucionais" oncomplete="ativaDias('formEmprestimosInstitucionais:tipoEmprestimoInstitucional');">
											<h:graphicImage url="/img/delete.gif" />
										</a4j:commandLink>
									</t:column>
								</t:dataTable>
								
							</td>
						</tr>
					
					</c:if>
					
				</table>

				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton id="cmdRealizarEmprestimosInstitucional" value="#{emprestimoInstitucionalMBean.confirmButton}" action="#{emprestimoInstitucionalMBean.realizarEmprestimos}" disabled="#{emprestimoInstitucionalMBean.qtdMateriaisEmprestimo <= 0 }" /> 
						<h:commandButton id="cmdVoltarListagem"  value="<< Voltar" action="#{emprestimoInstitucionalMBean.telaListaEmprestimosInstitucionais}" /> 
						<h:commandButton id="cancelar" value="Cancelar" action="#{emprestimoInstitucionalMBean.cancelar}" onclick="#{confirm}" immediate="true"  />
					</td>
				</tr>
			</tfoot>
		</table>
		
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
		
	</h:form>
		
</f:view>

<script type="text/javascript">

	//retorna o codigo da tecla a partir do evento passado
	//como par�metro
	function getCodigoTecla(evento) {
		var tecla = "";
		if (isIe())
			tecla = evento.keyCode;
		else
			tecla = evento.which;

		if (tecla == 13){
			document.getElementById('formEmprestimosInstitucionais:botaoAdicionar').click();
			return false;
		}
		
		return true;
		
	}	

	function isIe() {
		return (typeof window.ActiveXObject != 'undefined');
	}
			

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>