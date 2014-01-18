<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>


<%@page import="br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante"%>

<c:set var="STATUS_INSCRICAO_APROVADO" value="<%= StatusInscricaoParticipante.APROVADO %>" scope="request" />

<style type="text/css">
	strong { font-weight: bold; }
</style>

<%@ taglib uri="/tags/struts-html" prefix="html"%>

	
	<h2> Meus Cursos e Eventos </h2>
	
	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<p>Abaixo est�o listadas suas inscri��es realizadas nos cursos ou eventos oferecidos pela ${ configSistema['siglaInstituicao'] }. </p>
		<br />
		<p><strong>Por padr�o s�o mostradas apenas as inscri��es realizadas no �ltimo ano. Mas � poss�vel procurar inscri��es mais antigas. </strong> </p>
	</div>	
	
	<h:form id="formListaCursosEventosExtensao">    

		<table class="formulario" style=" width: 50%">
			<caption>Buscar Inscri��es</caption>
			<tbody>
				<tr>
	                <td><label for="frmBuscar:selectBuscaPeriodo">Per�odo da Inscri��o:</label></td>
	                <td><t:inputCalendar id="periodoInicio" value="#{gerenciaMeusCursosEventosExtensaoMBean.dataInicioInscricao}" renderAsPopup="true" 
	                            renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
	                            popupTodayString="Data de In�cio" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
	                            onchange="javascript:$('frmBuscar:selectBuscaPeriodo').checked = true;">
	                        <f:converter converterId="convertData" />
	                    </t:inputCalendar> 
	                    <i>at�</i> 
	                    <t:inputCalendar id="periodoFim" value="#{gerenciaMeusCursosEventosExtensaoMBean.dataFinalInscricao}" renderAsPopup="true" 
	                            renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
	                            popupTodayString="Data Final" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
	                            onchange="javascript:$('frmBuscar:selectBuscaPeriodo').checked = true;">
	                        <f:converter converterId="convertData" />
	                    </t:inputCalendar></td>
	            </tr> 
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Filtrar Inscri��es" actionListener="#{gerenciaMeusCursosEventosExtensaoMBean.filtarInscricoesUsuario}" id="codFiltar" />&nbsp;
	    			</td>
	    		</tr>
			</tfoot>
		</table>

		<c:choose>
		
			<c:when test="${gerenciaMeusCursosEventosExtensaoMBean.qtdInscricoesRealizadas == 0}">
				<div style="color:red; text-align: center; font-style: italic; margin-top: 40px;">
					N�o existe nenhuma inscri��o nos cursos e eventos de extens�o para o per�odo buscado
				</div>
			</c:when>
	
			<c:otherwise>
				
					<div class="infoAltRem" style="margin-top: 20px;">
		    			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Acessar Inscri��o
					</div>
					<table class="listagem" width="100%" cellpadding="0" cellspacing="0">
						<caption class="listagem">Minhas Inscri��es em Cursos E Eventos ( ${gerenciaMeusCursosEventosExtensaoMBean.qtdInscricoesRealizadas} ) </caption>
						<thead>
							<tr>
								<th colspan="2" style="width: 69%;">T�tulo</th>
								<th style="width:20%;">Tipo</th>
								<th style="text-align:center; width: 10%;">Status Inscri��o</th>
								<th width="1%" />
							</tr>
						</thead>
						<tbody>
							
							<c:set var="status" value="0" scope="request" />
							
							
							<%-- Interaje sofre as inscri��es de atividade 
							  -- Para cada inscri��o de atividade encontradas, interaje para imprirmir as inscri��o de mini atividades  
							  -- Achei mais f�cil fazer assim do quer trazer ordenado da busca.
							  --%>
							
							<c:forEach items="#{gerenciaMeusCursosEventosExtensaoMBean.inscricoesRealizadas}" var="inscricao">
								
								<c:if test="${inscricao.inscricaoAtividade.inscricaoAtividade}">
								
									<tr class="${status % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
										
										
										
											<td colspan="2" style="text-align:left;">${inscricao.inscricaoAtividade.atividade.titulo} </td>
											<td style="text-align:left;">${inscricao.inscricaoAtividade.atividade.tipoAtividadeExtensao.descricao}</td>
					                    
					                    	 <td style="text-align:center; ${ inscricao.statusInscricao.id == STATUS_INSCRICAO_APROVADO ? 'color:green; font-weight:bold;' : ''}">
					                    		${inscricao.statusInscricao.descricao}
					                    	 </td>
					                    	
					                    	<td align="center">
											<h:commandLink title="Acessar Inscri��o" action="#{gerenciaMeusCursosEventosExtensaoMBean.acessarInscricaoSelecionada}">
													<f:param name="idInscricaoParticipanteSelecionada" value="#{inscricao.id}" />
													<h:graphicImage url="/img/seta.gif" />
												</h:commandLink>
											</td>
					                    	
					                    	<c:set var="status" value="${status+1}" scope="request" />
					                </tr>
					                
								 </c:if>
								 	
							 	<c:if test="${! inscricao.inscricaoAtividade.inscricaoAtividade}">
							 	
								 	 <tr class="${status % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"  onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">      
						                    
											<td style="width: 20px;"></td>
											<td style="text-align:left; font-style: italic;">${inscricao.inscricaoAtividade.subAtividade.titulo} </td>
											<td style="text-align:left; font-style: italic;">${inscricao.inscricaoAtividade.subAtividade.tipoSubAtividadeExtensao.descricao}</td>
											
											<td style="text-align:center; ${ inscricao.statusInscricao.id == STATUS_INSCRICAO_APROVADO ? 'color:green; font-weight:bold;' : ''}">
					                    		${inscricao.statusInscricao.descricao}
					                    	 </td>
				                    	
					                    	<td align="center">
												<h:commandLink title="Acessar Inscri��o" action="#{gerenciaMeusCursosEventosExtensaoMBean.acessarInscricaoSelecionada}">
														<f:param name="idInscricaoParticipanteSelecionada" value="#{inscricao.id}" />
														<h:graphicImage url="/img/seta.gif" />
												</h:commandLink>
											</td>
											
											<c:set var="status" value="${status+1}" scope="request" />
						              </tr>
							 	</c:if>
				                
							</c:forEach>
						</tbody>
					</table>
				
			</c:otherwise>
			
		</c:choose>
		
		
		
		
		<c:choose>
		
			<c:when test="${gerenciaMeusCursosEventosExtensaoMBean.qtdParticipacoesSemInscricao > 0}">
				<div class="infoAltRem" style="margin-top: 20px;">
		    			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Acessar Inscri��o
					</div>
					<table class="listagem" width="100%" cellpadding="0" cellspacing="0">
						<caption class="listagem">Minhas Participa��es em Cursos E Eventos sem Inscri��o ( ${gerenciaMeusCursosEventosExtensaoMBean.qtdParticipacoesSemInscricao} ) </caption>
						<thead>
							<tr>
								<th colspan="2" style="width: 69%;">T�tulo</th>
								<th style="width:30%;">Tipo</th>
								<th width="1%" />
							</tr>
						</thead>
						<tbody>
							
							<c:set var="status2" value="0" scope="request" />
							
							
							<%-- Intereje sofre as inscri��es de atividade 
							  -- Para cada inscri��o de atividade encontradas, interaje para imprirmir as inscri��o de mini atividades  
							  -- Achei mais f�cil fazer assim do quer trazer ordenado da busca.
							  --%>
							
							<c:forEach items="#{gerenciaMeusCursosEventosExtensaoMBean.participacaoSemInscricao}" var="participante">
								
								<c:if test="${participante.participanteAtividadeExtensao}">
								
									<tr class="${status2 % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
										
										
										
											<td colspan="2" style="text-align:left;">${participante.atividadeExtensao.titulo} </td>
											<td style="text-align:left;">${participante.atividadeExtensao.tipoAtividadeExtensao.descricao}</td>
					                    
					                    
					                    	<td align="center">
											<h:commandLink title="Acessar Inscri��o" action="#{gerenciaMeusCursosEventosExtensaoMBean.acessarParticipacaoSelecionada}">
													<f:param name="idParticipanteSelecionado" value="#{participante.id}" />
													<h:graphicImage url="/img/seta.gif" />
												</h:commandLink>
											</td>
					                    	
					                    	<c:set var="status" value="${status+1}" scope="request" />
					                </tr>
								 	
								 	<c:forEach items="#{gerenciaMeusCursosEventosExtensaoMBean.participacaoSemInscricao}" var="participante2">
						               <tr class="${status2 % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"  onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">      
						                    <c:if test="${! participante2.participanteAtividadeExtensao && ( participante2.subAtividade.atividade.id == participante.atividadeExtensao.id ) }">
												<td style="width: 20px;"></td>
												<td style="text-align:left; font-style: italic;">${participante2.subAtividade.titulo} </td>
												<td style="text-align:left; font-style: italic;">${participante2.subAtividade.tipoSubAtividadeExtensao.descricao}</td>
		
		
						                    	<td align="center">
													<h:commandLink title="Acessar Inscri��o" action="#{gerenciaMeusCursosEventosExtensaoMBean.acessarParticipacaoSelecionada}">
															<f:param name="idParticipanteSelecionado" value="#{participante2.id}" />
															<h:graphicImage url="/img/seta.gif" />
													</h:commandLink>
												</td>
												
												<c:set var="status2" value="${status+1}" scope="request" />
												
						                    </c:if>
						               </tr>
				                    </c:forEach>
								 	
				                </c:if>
				                
							</c:forEach>
						</tbody>
					</table>
			</c:when>
			
		</c:choose>
		
		
		
	</h:form>