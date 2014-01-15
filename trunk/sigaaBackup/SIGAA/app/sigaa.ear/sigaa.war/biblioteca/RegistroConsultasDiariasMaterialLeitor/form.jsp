<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js"> </script>


<f:view>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<a4j:keepAlive beanName="registroConsultasDiariaisMateriaisLeitorMBean" />
	
	<h:form id="formRetistraConsultaDiariaisMateriasLeitor">
	
		<a4j:region>
	
		<h2><ufrn:subSistema /> &gt; Cadastrar Consulta Local Usando Leitor Ótico</h2>
	
		<div class="descricaoOperacao" style="width:85%">
			<p>Caro usuário, use esta operação para registrar as consultas realizadas diariamente pelos usuário que frequentam a biblioteca.</p>
			<p>Digite o código de barras do material e pressione <em>enter</em> para adicioná-lo. <strong>(Este processo pode ser realizado usando-se um leitor de códigos de barras) </strong></p>
			<p>Quando terminar, escolha o turno e a data nos quais os materiais foram consultados e pressione o botão <strong>Registrar Consultas</strong>.</p>
		</div>
		
		
		<table class="formulario" width="85%">
				
			<caption>Cadastrar Consultas de Materiais</caption>
				
			<tr>
				<td>
					<table class="subformulario" width="100%">
						
						<caption>Códigos de Barras dos Materiais Consultados</caption>
						
						<thead>
							<tr>
								<th style="text-align:center;">
									Código de Barras: <h:inputText id="campoCB" 
										onkeypress="return executaClickBotao(event, 'formRetistraConsultaDiariaisMateriasLeitor:botaoAdicionar' )" value="#{registroConsultasDiariaisMateriaisLeitorMBean.codigoBarras}" />
										
										<a4j:commandButton style="margin-left:10px;" id="botaoAdicionar" title="Adicionar Código de Barras"
												actionListener="#{registroConsultasDiariaisMateriaisLeitorMBean.adicionarMaterial}"
												reRender="formRetistraConsultaDiariaisMateriasLeitor" value="Adicionar"
												oncomplete="document.getElementById('formRetistraConsultaDiariaisMateriasLeitor:campoCB').value = ''; document.getElementById('formRetistraConsultaDiariaisMateriasLeitor:campoCB').focus();"/>
						
										<a4j:status>
											<f:facet name="start">
												<h:graphicImage value="/img/indicator.gif" />
											</f:facet>
										</a4j:status>
						
								</th>
							</tr>
						</thead>
						
						<tr>
							<td>
								<c:if test="${empty registroConsultasDiariaisMateriaisLeitorMBean.codigosBarras}">
									<p style="color:#FF0000;text-align:center;">Não há códigos de barras adicionados para o registro.</p>
								</c:if>
								
								<p style="line-height:25px;">
									<c:forEach var="c" items="#{registroConsultasDiariaisMateriaisLeitorMBean.codigosBarras}">
										${c}&nbsp;&nbsp;&nbsp;&nbsp;
									</c:forEach>
								</p>
							</td>
						</tr>
					</table>
				</td>
			</tr>
				
				
			<tr>
			
				<td>
					<table class="subFormulario" style="width:100%;">
					
						<caption>Data e Turno da Consulta</caption>
					
						<tr>
							<th class="obrigatorio">Data da Consulta:</th>
							<td><t:inputCalendar id="Data" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" value="#{registroConsultasDiariaisMateriaisLeitorMBean.data}" onkeypress="return formataData(this,event)" popupDateFormat="dd/MM/yyyy" /></td>
						</tr>
						<tr>
							<th>Turno da Consulta:</th>
							<td>
								<h:selectOneRadio value="#{registroConsultasDiariaisMateriaisLeitorMBean.turno}">
									<f:selectItem itemLabel="Matutino" itemValue="1" />
									<f:selectItem itemLabel="Vespertino" itemValue="2" />
									<f:selectItem itemLabel="Noturno" itemValue="3" />
								</h:selectOneRadio>
							</td>
						</tr>
					</table>
				
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
							, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
							, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
								<h:commandButton value="Registrar Consultas" action="#{registroConsultasDiariaisMateriaisLeitorMBean.registrarConsultas}" id="botaoRegistrarConsulta" /> 
						</ufrn:checkRole>
						
						<h:commandButton value="Cancelar" action="#{registroConsultasDiariaisMateriaisLeitorMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelarRegistroConsulta" />
						
					</td>
				</tr>
			</tfoot>
				
		</table>

		</a4j:region>
	</h:form>

	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
	
</f:view>

<script type="text/javascript">
	document.getElementById('formRetistraConsultaDiariaisMateriasLeitor:campoCB').focus();
	
	//função que executa o click no botao passado quando o usuário pressiona o enter
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

	// testa se é o IE ou não
	function isIe() {
		return (typeof window.ActiveXObject != 'undefined');
	}
	
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>