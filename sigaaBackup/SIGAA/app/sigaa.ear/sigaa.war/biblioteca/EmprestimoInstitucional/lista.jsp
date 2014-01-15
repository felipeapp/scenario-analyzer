<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<a4j:keepAlive beanName="emprestimoInstitucionalMBean" />

	<h2><ufrn:subSistema /> &gt; Empr�stimo Institucional &gt; ${emprestimoInstitucionalMBean.tipoBiblioteca }</h2>
	
	<h:form>
	
	
		<%--  Parte onde o usu�rio visualiza o comprovante da renova��o   --%>
		<c:if test="${emprestimoInstitucionalMBean.habilidaComprovanteRenovacao}">
			
				<table  class="subFormulario" align="center" style="margin-top: 30px;">
					<caption style="text-align: center;">Impress�o Comprovante</caption>
					<tr>
						<td width="8%" valign="middle" align="center">
							<html:img page="/img/warning.gif"/>
						</td>
						<td valign="middle" style="text-align: justify">
							Por favor, para uma maior seguran�a imprima o comprovante da renova��o clicando no link ao lado.
						</td>
						<td>
							<table>
								<tr>
									<td align="center">
								 		<h:graphicImage url="/img/printer_ok.png" />
								 	</td>
								 </tr>
								 <tr>
								 	<td style="font-size: medium;">
								 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{emprestimoInstitucionalMBean.geraComprovanteRenovacao}"  />
								 	</td>
								 </tr>
							</table>
						</td>
					</tr>
				</table>
			<br/>
			
		</c:if>



		<%--  Parte onde o usu�rio visualiza o comprovante da devolu��o   --%>
		<c:if test="${emprestimoInstitucionalMBean.habilidaComprovanteDevolucao}">
			
				<table  class="subFormulario" align="center" style="margin-top: 30px;">
					<caption style="text-align: center;">Impress�o Comprovante</caption>
					<tr>
						<td width="8%" valign="middle" align="center">
							<html:img page="/img/warning.gif"/>
						</td>
						<td valign="middle" style="text-align: justify">
							Por favor, para uma maior seguran�a imprima o comprovante da devolu��o clicando no link ao lado.
						</td>
						<td>
							<table>
								<tr>
									<td align="center">
								 		<h:graphicImage url="/img/printer_ok.png" />
								 	</td>
								 </tr>
								 <tr>
								 	<td style="font-size: medium;">
								 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{emprestimoInstitucionalMBean.geraComprovanteDevolucao}"  />
								 	</td>
								 </tr>
							</table>
						</td>
					</tr>
				</table>
			<br/>
			
		</c:if>
		
	
		<div class="infoAltRem" style="width:95%;margin-top:15px;">
			<h:graphicImage value="/img/adicionar.gif" />
			<h:commandLink value="Novo Empr�stimo Institucional" action="#{emprestimoInstitucionalMBean.preCadastrar}" />
		</div>
	
		<table class="formulario" style="width: 90%">
			<caption>Filtros da Busca</caption>
			
			<tr>
			<c:if test="${not emprestimoInstitucionalMBean.emprestimoParaBibliotecaExterna}">
				<th class="required">Biblioteca do Empr�stimo: </th>
				<td>
					<h:selectOneMenu value="#{emprestimoInstitucionalMBean.biblioteca.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItems value="#{emprestimoInstitucionalMBean.bibliotecasInternas}" />
					</h:selectOneMenu>
					<ufrn:help>Biblioteca para onde os materiais foram empr�stados.</ufrn:help> 
				</td>
			</c:if>
			
			<c:if test="${emprestimoInstitucionalMBean.emprestimoParaBibliotecaExterna}">
				<th class="required">Biblioteca Externa do Empr�stimo:</th>
				<td>
					<h:selectOneMenu value="#{emprestimoInstitucionalMBean.biblioteca.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItems value="#{emprestimoInstitucionalMBean.bibliotecasExternas}" />
					</h:selectOneMenu>
					<ufrn:help>Biblioteca Externa para onde os materiais foram empr�stados.</ufrn:help>
				</td>
			</c:if>
			</tr>
			
			<tr>
				<td colspan="2">
					<%-- Exibe as informa��es do usu�rio. --%>
					<c:set var="_infoUsuarioCirculacao" value="${emprestimoInstitucionalMBean.infoUsuario}" scope="request" />
					<c:set var="_situacoesUsuario" value="${emprestimoInstitucionalMBean.situacoesUsuario}" scope="request" />
					<c:set var="_mostrarFoto" value="false" scope="request" />
					<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
				</td>
			</tr>
			
			<tr>
				<th>Situa��o dos Empr�stimos:</th>
				<td>
					<h:selectOneRadio value="#{emprestimoInstitucionalMBean.situacao}">
						<f:selectItem itemLabel="Ativo" itemValue="#{emprestimoInstitucionalMBean.situacaoAtiva}"/>
						<f:selectItem itemLabel="Devolvido" itemValue="#{emprestimoInstitucionalMBean.situcaoDevolvido}"/>
						<f:selectItem itemLabel="Ambas" itemValue="#{emprestimoInstitucionalMBean.situacaoAmbas}"/>
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th>Per�odo do Empr�stimo:</th>
				<td>
					De <t:inputCalendar id="Inicio" value="#{emprestimoInstitucionalMBean.dataInicio}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
					At� <t:inputCalendar id="Fim" value="#{emprestimoInstitucionalMBean.dataFim}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
				</td>
			</tr>
			
			
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="cmdButtomBucarEmprestimosInstitucionais"         value="Buscar" action="#{emprestimoInstitucionalMBean.buscarEmprestimosInstitucionais}"/>
						<h:commandButton id="cmdButtomApagaResulatadoBusca"         value="Apagar" title="Apagar resultado da consulta" action="#{emprestimoInstitucionalMBean.apagarEmprestimosInstitucionais}"/>
						<h:commandButton id="cmdButtomCancelarBuscaEmprestimosInstitucionais" value="Cancelar" action="#{emprestimoInstitucionalMBean.cancelar}" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>




		
		
		
		
		
		
		
		
		
		

		<%-- Resultados da pesquisa --%>

		<c:if test="${emprestimoInstitucionalMBean.size > 0}">


	
				<div class="infoAltRem" style="width:95%;margin-top:15px;">
		
					<h:graphicImage value="/img/cal_prefs.png" style="overflow: visible;" />: 
					Renovar Empr�stimo
					<h:graphicImage value="/img/del_cal.png" style="overflow: visible;" />: 
					Devolver Material
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
						<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: 
						Estornar Empr�stimo
					</ufrn:checkRole>
				</div> 
		
				<table class="listagem" style="width:100%;">
				
					<caption>Lista de Empr�stimos realizados para ${emprestimoInstitucionalMBean.all[0].usuarioBiblioteca.biblioteca.descricaoCompleta} (${emprestimoInstitucionalMBean.size})</caption>
					
					<c:if test="${emprestimoInstitucionalMBean.size == 0}">
						<tr><td style='text-align:center;color:red;'>N�o foram encontrados empr�stimos com esses par�metros de busca.</td></tr>
					</c:if>
					
					<c:if test="${emprestimoInstitucionalMBean.size > 0}">
						<thead>
							<tr>
								<th style="text-align:left;width:50%">Material</th>
								<th style="text-align:center;width:120px;">Data do Empr�stimo</th>
								<th style="text-align:center;width:120px;">Prazo</th>
								<th style="text-align:center;width:120px;">Data de Renova��o</th>
								<th style="text-align:center;width:120px;">Data de Devolu��o</th>
								<th style="width: 1%"></th>
								<th style="width: 1%"></th>
								
								<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
									<th style="width: 1%"></th>
								</ufrn:checkRole>
							</tr>
						</thead>
						
						
						<c:forEach items="#{emprestimoInstitucionalMBean.all}" var="e" varStatus="status">
							
							
							<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
								<td style="text-align:left;">${e.material.informacao}</td>
								<td style="text-align:center;"><ufrn:format type="dataHora" valor="${e.dataEmprestimo}"/></td>
								<td style="text-align:center;"><ufrn:format type="dataHora" valor="${e.prazo}"/></td>
								<td style="text-align:center;"><ufrn:format type="dataHora" valor="${e.dataRenovacao}"/></td>
								<td style="text-align:center;"><ufrn:format type="dataHora" valor="${e.dataDevolucao}"/></td>
								
								<c:if test="${e.dataDevolucao == null}">
									<td>
										<h:commandLink title="Removar Empr�stimos"
											action="#{emprestimoInstitucionalMBean.renovar}" rendered="#{e.podeRenovar}"
											 onclick="if (!confirm('Confirma a renova��o deste empr�stimo?')) return false;">
											<f:param name="id" value="#{e.id}" />
											<h:graphicImage url="/img/cal_prefs.png" alt="Renovar Material" />
										</h:commandLink>
									</td>
									<td>
										<h:commandLink title="Devolver Material"
											action="#{emprestimoInstitucionalMBean.devolver}" onclick="if (!confirm('Confirma a devolu��o deste empr�stimo?')) return false;">
											<f:param name="id" value="#{e.id}" />
											<h:graphicImage url="/img/del_cal.png" alt="Devolver Material" />
										</h:commandLink>
									</td>
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
										<td>
											<h:commandLink title="Estornar Empr�stimo"
												action="#{emprestimoInstitucionalMBean.estornar}" onclick="if (!confirm('Tem certeza que deseja estornar este empr�stimo?')) return false;">
												<f:param name="id" value="#{e.id}" />
												<h:graphicImage url="/img/biblioteca/estornar.gif" alt="Estornar Empr�stimo" />
											</h:commandLink>
										</td>
									</ufrn:checkRole>
								</c:if>
								
								
								<c:if test="${e.dataDevolucao != null}">
									<td></td>
									<td></td>
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
										<td></td>
									</ufrn:checkRole>
								</c:if>
							</tr>
						</c:forEach>
					</c:if>
				</table>
		
		</c:if>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>