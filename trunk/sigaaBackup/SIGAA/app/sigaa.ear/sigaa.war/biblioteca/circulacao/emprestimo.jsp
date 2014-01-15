<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<%-- Java script para ativar o campo onde o usuário informa a quantiade de dias do empréstimo personalizado --%>
	<script type="text/javascript">		

	    /* Função que contém a lógica para mostrar e ocutar a quantidade de dias para os empréstimos personalizados
	     * sempre chamada quando a página é garregada, porque senão, o campo dias era ocultado sempre que a página era recarregada
	     */
		function ativaDias (idselect){
	
			var valor = document.getElementById(idselect).value;
	
			if (valor == undefined)
				valor = select.val();
			
			// se a linha para escolher o tipo de emprétimo está sendo mostrada e o tipo de empréstimo escolhido 
			// é personalizável, mostra a quantidade de dias
			if (valor == <h:outputText value="#{moduloCirculacaoMBean.idTipoEmprestimoPersonalizado}"/> 
					&& document.getElementById('linhaTipoEmprestimo').style.display != 'none' ){
				document.getElementById('linhaDiasAEmprestarTH').style.display = '';
				document.getElementById('linhaDiasAEmprestarTD').style.display = '';
				document.getElementById('senhaOperador').style.display = 'block';
				document.getElementById('formRealizaEmprestimos:qtdDiasEmprestimo').focus();
			} else {
				document.getElementById('linhaDiasAEmprestarTH').style.display = 'none';
				document.getElementById('linhaDiasAEmprestarTD').style.display = 'none';
				document.getElementById('senhaOperador').style.display = 'none';	
			}
		}

		function focarCampo() {
			<c:if test="${moduloCirculacaoMBean.material == null}">
			document.getElementById('formRealizaEmprestimos:inputTextCodigoBarras').focus();
			</c:if>
			
			<c:if test="${moduloCirculacaoMBean.material != null}">
			document.getElementById('formRealizaEmprestimos:inputSecretSenhaBiblioteca').focus();
			</c:if>
		}
		
		window.onload = function() {
			focarCampo();
			ativaDias('formRealizaEmprestimos:selecTipoEmprestimo');
		};
		
		
	
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


	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="moduloCirculacaoMBean" />

	<h:form id="formRealizaEmprestimos">
	
	<h2><ufrn:subSistema /> &gt; Módulo de Circulação &gt; Empréstimos</h2>
	
	<div class="descricaoOperacao" style="width:80%;">
		<p>Para realizar um empréstimo para o usuário escolhido, selecione um material e o tipo de empréstimo.</p>
	</div>
	
	
	<c:set var="usuarioImpedidoFazerEmpretimos" value="${moduloCirculacaoMBean.usuarioImpedidoFazerEmpretimos}" scope="request" />
	
	
	<%-- Exibe as informações do usuário. --%>
	<c:set var="_infoUsuarioCirculacao" value="${moduloCirculacaoMBean.infoUsuario}" scope="request" />
	<c:set var="_situacoesUsuario" value="${moduloCirculacaoMBean.situacoesUsuario}" scope="request" />

	<c:if test="${usuarioImpedidoFazerEmpretimos}">	
		<c:set var="_inativo" value="${true}" scope="request" />
	</c:if>
	
	
	
	<br>
		<table id="tableFormularioEmprestimo" class="formulario" style="width:100%;">
		
			<caption>Realizar Empréstimo  </caption>
			<tbody>
			
				
				<tr>
					<td colspan="2">
						<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
					</td>
				</tr>
			
				
				<tr>
					<td colspan="2">
						<table id="tableDadosNovoEmprestimo" class="subFormulario" style="width: 100%;">
							<caption> Novo Empréstimo </caption>
							
							
							<c:if test="${moduloCirculacaoMBean.material != null}">
							
								<tr>
									<th style="vertical-align:top;">Material: &nbsp;</th>
									<td>${moduloCirculacaoMBean.material.informacao}</td>
								</tr>
								<tr>
									<th style="vertical-align:top;">Status: &nbsp;</th>
									<td>${moduloCirculacaoMBean.material.status.descricao}</td>
								</tr>
								<tr>
									<th style="vertical-align:top;">Tipo do Material: &nbsp;</th>
									<td>${moduloCirculacaoMBean.material.tipoMaterial.descricao}</td>
								</tr>
								<tr>
									<th></th>
									<td>
										<strong>
											<c:if test="${moduloCirculacaoMBean.material.emprestado}">
												<p style='color:#FF0000;'>Material Emprestado para: ${moduloCirculacaoMBean.emprestimoDoMaterial.usuarioBiblioteca.nome}</p>
											</c:if>
											
											<c:if test="${not moduloCirculacaoMBean.material.emprestado}">
												<c:if test="${moduloCirculacaoMBean.material.disponivel}">
													<p style='color:#00AA00;'>Material Disponível.</p>
												</c:if>
												
												<c:if test="${not moduloCirculacaoMBean.material.disponivel}">
													<p style='color:#FF0000;'>Material Indisponível.</p>
												</c:if>
											</c:if>
										</strong>
									</td>
								</tr>
								<tr>
									
									<td colspan="2" style="text-align: center; height: 40px;">
										<a4j:outputPanel ajaxRendered="true">
											<h:outputText id="outputTextDescricaoPolitica" value="#{moduloCirculacaoMBean.descricaoPoliticaASerUtilizada}" />
										</a4j:outputPanel>
									</td>
								</tr>	
								
							</c:if>
							<c:if test="${moduloCirculacaoMBean.material.id == null }">
								<tr>
									<th>Material:</th>
									<td style="font-weight:bold;">Material não informado</td>
								</tr>
							</c:if>
						
							<tr>
								<th style="width:150px;">Código de Barras: </th>
								<td>
									<h:inputText size="12" maxlength="20" id="inputTextCodigoBarras" value="#{moduloCirculacaoMBean.codigoBarras}" 
											onkeypress="return executaClickBotao(event, 'formRealizaEmprestimos:botaoBuscaMaterial' )"/>
									<h:commandButton id="botaoBuscaMaterial" value="Buscar" action="#{moduloCirculacaoMBean.buscarMaterial}" />
								</td>
							</tr>
				
							<c:if test="${ fn:length(moduloCirculacaoMBean.tiposEmprestimos) > 0 }">
								<tr id="linhaTipoEmprestimo" ${moduloCirculacaoMBean.material.id == null ? "style='display:none;'" : ""}>
									<th class="obrigatorio">Tipo de Empréstimo:</th>
									<td>
										<h:selectOneMenu id="selecTipoEmprestimo" value="#{moduloCirculacaoMBean.idTipoEmprestimo}" disabled="#{moduloCirculacaoMBean.material.emprestado}"
												onkeyup="ativaDias('formRealizaEmprestimos:selecTipoEmprestimo'); focarCampo();">
											<f:selectItem itemValue="-1" itemLabel=" -- SELECIONE -- " />
											<f:selectItems value="#{moduloCirculacaoMBean.tiposEmprestimos}" />
											<a4j:support actionListener="#{moduloCirculacaoMBean.mostraInformacaoPoliticaEmprestimo}" event="onchange" 
											reRender="outputTextDescricaoPolitica"
											oncomplete="ativaDias('formRealizaEmprestimos:selecTipoEmprestimo'); focarCampo();"></a4j:support>
										</h:selectOneMenu>
										<ufrn:help>Escolha o tipo do empréstimo para o material atual.</ufrn:help>
									</td>
								</tr>
								
								<tr>
									<th id="linhaDiasAEmprestarTH" style="display:none;" class="obrigatorio">Dias a Emprestar:</th>
									<td id="linhaDiasAEmprestarTD" style="display:none;">
										<h:inputText id="qtdDiasEmprestimo" size="3" maxlength="3" value="#{moduloCirculacaoMBean.diasAEmprestar}" disabled="#{moduloCirculacaoMBean.material.emprestado}" 
											onkeyup="return formatarInteiro(this);" />
										<ufrn:help>A quantidade de dias que o usuário vai poder ficar com o material.</ufrn:help>
									</td>
								</tr>
								
							</c:if>
							
							
							<tr>
								<td colspan="2">
									<c:if test="${moduloCirculacaoMBean.material != null}">
										<table style="width: 100%; margin-left: auto; margin-right: auto;">
											<tr>
												<th style="font-weight: bold; width: 50%">Senha da Biblioteca:</th>
												<td> 
													<h:inputSecret id="inputSecretSenhaBiblioteca"  value="#{moduloCirculacaoMBean.senhaBiblioteca}"  
													onkeypress="return executaClickBotao(event, 'formRealizaEmprestimos:botaoRealizarEmprestimos' )" onkeyup="return formatarInteiro(this);" size="12" maxlength="8" />
												</td>
											</tr>
											<tr>
												<td colspan="2">
													<div id="senhaOperador" style="display:none;">
														<c:set var="exibirApenasSenha" value="true" scope="request"/>
														<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
													</div>
												</td>
											</tr>
										</table>
										</c:if>
								</td>
							</tr>
							
							
							
						</table>
					</td>
				</tr>
			
				
				
				
				
			
			<tr>
				<td colspan="2">
					
						<t:div rendered="#{moduloCirculacaoMBean.emprestimosAtivos != null}">
						<table id="tableEmprestimosAtivos" class="subFormulario" style="width: 100%;">
							<caption> Empréstimos Ativos do Usuário </caption>
							
							<thead>
								<tr>
									<th>Informações do Material</th>
									<th style='text-align: center;'>Data do Empréstimo</th>
									<th>Tipo do Empréstimo</th>
									<th style='text-align: center;'>Prazo</th>
									<th style='text-align: center;'>Atrasado</th>
								</tr>
							</thead>
					
							<c:if test="${not empty moduloCirculacaoMBean.emprestimosAtivos}">		
								<c:forEach items="#{moduloCirculacaoMBean.emprestimosAtivos}" var="e" varStatus="status">
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<td style="width: 50%;">${e.material.informacao}</td>
										<td style="text-align:center">
											<h:outputText value="#{e.dataEmprestimo}" converter="convertData" />
										</td>
										<td  style="text-align:center">${e.politicaEmprestimo.tipoEmprestimo.descricao}</td>
										<td style="text-align:center">
											<h:outputText value="#{e.prazo}" converter="convertData">
												<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss" />
											</h:outputText>
										</td>
										<c:if test="${e.atrasado}">
											<td width="6%" style="text-align:center;color:red">
												SIM
											</td>
										</c:if>
										<c:if test="${ ! e.atrasado}">
											<td width="6%" style="text-align:center;color:green">
												NÃO
											</td>
										</c:if>
									</tr>
			
								</c:forEach>
							</c:if>
							<c:if test="${ empty moduloCirculacaoMBean.emprestimosAtivos}">		
								<tr> 
									<td style="color:red; text-align: center;" colspan="5">O usuário não possui empréstimos ativos</td>
								</tr>
							</c:if>
						</table>
					</t:div>
				</td>
			</tr>
			
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="botaoRealizarEmprestimos" value="Realizar Empréstimo" action="#{moduloCirculacaoMBean.realizarOperacao}" 
							disabled="#{usuarioImpedidoFazerEmpretimos|| moduloCirculacaoMBean.material.emprestado || moduloCirculacaoMBean.material == null}"/>
						<h:commandButton id="botaoVoltarTelaEmprestimos" value="Cancelar" action="#{moduloCirculacaoMBean.voltaTelaBusca}"  immediate="true" onclick="#{confirm}"  />
					</td>
				</tr>
			</tfoot>
		</table>

		
		
	</h:form>
	
</f:view>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>