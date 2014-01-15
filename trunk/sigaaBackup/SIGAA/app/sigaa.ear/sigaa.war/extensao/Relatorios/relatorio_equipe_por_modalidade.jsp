<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>

.subtitulo {
	text-align: left;
	background: #EDF1F8;
	color: #333366;
	font-variant: small-caps;
	font-weight: bold;
	letter-spacing: 1px;
	margin: 1px 0;
	border-collapse: collapse;
	border-spacing: 2px;
	font-size: 1em;
	font-family: Verdana, sans-serif;
	font-size: 12px
}


</style>



<f:view>

	<h2><ufrn:subSistema /> > Total de Categoria de Membros por Modalidade</h2>
	
	
	
	
	<h:form id="form">
	
		<div class="descricaoOperacao">
			<p>Senhor(a) usuário(a), <br/><br/>
			
				Este é o relatório nominal do Total de Membros por tipo(Docente,Discente,Servidor,Externo). Com este relatório o(a) senhor(a) pode visualizar <br/>
				o total de membros Discentes, Docentes, Servidores e Externos presentes por Ações de Extensão, bem como o total de membros <br/>
				de cada Ação Extensionista. Esta disposto também um resumo(no final da página) contendo o total de Discentes, Docentes, Servidores<br/>
				e Externos de todas as Ações Extensionistas, como também o total de membros de todas as Ações.<br><br/>						
			
			
				<b>Atenção</b><br/>
				Alguns Membros podem ter o nome na cor Azul, o que significa que tais Membros são repetidos, ou seja, são membros<br/>
				de mais de uma ação ou está em uma mesma ação mais de uma vez, exercendo funções diferentes.<br/>
			</p>
		
		</div>	

		<table class="formulario" width="80%">
			<caption>Consultar Relatório Gerais de Extensão</caption>
			<tbody>			

				
				<tr>
					<th class="required">Tipo da Ação:</th>
					<td>
					 	<h:selectOneMenu id="buscaTipoAcao" value="#{relatoriosAtividades.tipoAtividadeExtensao.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
					 		<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
				    	</h:selectOneMenu>	    	 
					</td>
				</tr>		


				<tr>
			    	<th class="required"> <label for="situacaoAcao"> Situação da Ação: </label> </th>
			    	<td>
				    	 <h:selectOneMenu  id="buscaSituacao" value="#{relatoriosAtividades.situacaoAtividade.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				    	 	<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcaoExtensaoValidas}" />
			 			 </h:selectOneMenu>
			    	 
			    	</td>
			    </tr>


				<tr>
					<th class="required">Período de execução da ação:</th>
					<td>
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatoriosAtividades.dataInicio}" id="dataInicio" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
						a
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatoriosAtividades.dataFim}" id="dataFim" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
					</td>
				</tr>
		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Gerar Relatório" action="#{ relatoriosAtividades.gerarRelatorioEquipePorModalidade }"
							id="BotaoRelatorio" /> 
					<h:commandButton value="Cancelar" action="#{ relatoriosAtividades.cancelar }"
							id="BotaoCancelar" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
		

		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center>		
		
		
		<c:if test="${not empty relatoriosAtividades.atividadesLocalizadas}">
		
		<br/>
		<br/>
		
		<div class="infoAltRem">			
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação de Extensão					    		    
		</div>
		<br/>
		
		<table class="listagem">
		    <caption> Ações Encontradas ( ${ fn:length(relatoriosAtividades.atividadesLocalizadas) } ) </caption>
	
		      <thead>
		      	<tr>
					<th >Nome</th>
					<th ></th>		        		        		
		        	<th >Categoria</th>
		        	<th >Função</th>
		        	<th ></th>
		        	
		        	
		        </tr>
		 	</thead>		 	
		 	<tbody>	
		 	
		 	<c:set var="TOTAL"  value="0"/>	 	
		 		 	
	       	<c:set var="TOTAL_DOCENTE"  value="0"/>
		 	<c:set var="TOTAL_DISCENTE" value="0"/>
		 	<c:set var="TOTAL_SERVIDOR" value="0"/>
		 	<c:set var="TOTAL_EXTERNO"  value="0"/>
		 	
		 	<c:set var="DOCENTE"  value="1"/>
		 	<c:set var="DISCENTE"  value="2"/>
		 	<c:set var="SERVIDOR"  value="3"/>
		 	<c:set var="EXTERNO"  value="4"/>
		 	
		 	
		 	
		 	
	       	
	       	<c:forEach items="#{relatoriosAtividades.atividadesLocalizadas}" var="ativ" varStatus="status">  			
		 			
		 			<c:set var="TOTAL_DOCENTE_ACAO"  value="0"/>
		 			<c:set var="TOTAL_DISCENTE_ACAO" value="0"/>
		 			<c:set var="TOTAL_SERVIDOR_ACAO" value="0"/>
		 			<c:set var="TOTAL_EXTERNO_ACAO"  value="0"/>
	       	 		
	               <tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
	                    <td > ${ativ.codigo}</td>
	                    <td width="32%"> ${ativ.projeto.titulo}</td>
	                    <td></td>	                    
	                    <td></td>
	                    <td width="2%">					
							<h:commandLink title="Visualizar Ação de Extensão" action="#{atividadeExtensao.view}" id="visualizarAcaooExtensao">
							    <f:param name="id" value="#{ativ.id}"/>
			                	<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						</td>
					</tr>
						
					<c:set var="TAMANHO_EQUIPE_ACAO" value="${ fn:length(ativ.projeto.equipe) }" />					
					
					<c:forEach items="#{ativ.projeto.equipe}" var="mp" varStatus="loop">																			
							
							<c:if test="${  mp.categoriaMembro.id == DOCENTE }">														
								<c:set var="TOTAL_DOCENTE_ACAO"  value="${ TOTAL_DOCENTE_ACAO + 1 }"/>										
							</c:if>
							
							<c:if test="${  mp.categoriaMembro.id == DISCENTE }">														
								<c:set var="TOTAL_DISCENTE_ACAO"  value="${ TOTAL_DISCENTE_ACAO + 1 }"/>
							</c:if>
							
							<c:if test="${  mp.categoriaMembro.id == SERVIDOR }">
								<c:set var="TOTAL_SERVIDOR_ACAO"  value="${ TOTAL_SERVIDOR_ACAO + 1 }"/>		
							</c:if>
							
							<c:if test="${  mp.categoriaMembro.id == EXTERNO }">																					
								<c:set var="TOTAL_EXTERNO_ACAO"  value="${ TOTAL_EXTERNO_ACAO + 1 }"/>								
							</c:if>
							
												
							<tr>									
								<c:if test="${mp.selecionado}">
									<td width="40%"> <font color="blue">${mp.pessoa.nome}</font> </td>
								</c:if>									
									
								<c:if test="${!mp.selecionado}">
									<td width="40%"> ${mp.pessoa.nome} </td>
								</c:if>						
								<td></td>
								<td>${mp.categoriaMembro.descricao}</td>								
								<td width="45%">${mp.funcaoMembro.descricao}</td>
								<td></td>									
							</tr>					
																														
																			
						
						<%-- ÚLTIMO MEMBRO DESTA AÇÃO --%>
						<c:if test="${loop.index == TAMANHO_EQUIPE_ACAO - 1 }">						
							<tr>
								<td colspan="4">
									<br/> 
									<br/>
									<table class="subFormulario" width="50%">
										<caption style="text-align: center">Resumo desta Ação</caption>
								
										<tr>
											
											<td><b>Total de Membros Docentes desta Ação:</b></td>
											<td><b>${TOTAL_DOCENTE_ACAO}</b></td>
										</tr>
									
										<tr>
											<td><b>Total de Membros Discentes desta Ação:</b></td>
											<td><b>${TOTAL_DISCENTE_ACAO}</b></td>
										</tr>
									
										<tr>
											<td><b>Total de Membros Servidores desta Ação:</b></td>
											<td><b>${TOTAL_SERVIDOR_ACAO}</b></td>
										</tr>
									
										<tr>
											<td><b>Total de Membros Externos desta Ação:</b></td>
											<td><b>${TOTAL_EXTERNO_ACAO}</b></td>
										</tr>	
										
										<tr>
											<td><b>Total de Membros da Ação:</b></td>
											<td><b>${TOTAL_DOCENTE_ACAO + TOTAL_DISCENTE_ACAO + TOTAL_SERVIDOR_ACAO + TOTAL_EXTERNO_ACAO}</b></td>
										</tr>
																		
									</table>
									<br/>
									<br/>
									
							</tr>						
						</c:if>		 											              
	          </c:forEach>	   
	          			<c:set var="TOTAL_DOCENTE"  value="${ TOTAL_DOCENTE + TOTAL_DOCENTE_ACAO }"/>
		 				<c:set var="TOTAL_DISCENTE" value="${ TOTAL_DISCENTE + TOTAL_DISCENTE_ACAO }"/>
		 				<c:set var="TOTAL_SERVIDOR" value="${ TOTAL_SERVIDOR + TOTAL_SERVIDOR_ACAO }"/>
		 				<c:set var="TOTAL_EXTERNO"  value="${ TOTAL_EXTERNO + TOTAL_EXTERNO_ACAO }"/>       
	       </c:forEach>   
	          <c:set var="TOTAL"  value="${TOTAL_DOCENTE + TOTAL_DISCENTE + TOTAL_SERVIDOR + TOTAL_EXTERNO}"/>
	          
		 	</tbody>
		 </table>
		 
		 <br/>
		 <br/>
		 
		 <table class="subFormulario" width="40%">
		    <caption style="text-align: center"> Resumo Geral</caption>
		    
		    <tr> 
		    	<td> <b> Total de membros docentes das ações: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_DOCENTE} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de membros discentes das ações: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_DISCENTE} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de membros servidores das ações: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_SERVIDOR} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de membros externos das ações: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_EXTERNO} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de membros das ações: </b>  <td>
		    	<td align="right"> <b> ${TOTAL} </b> </td>		    
		    </tr>
		    
		 </table>
		 <br/>
		 		 
		 </c:if>
		
		
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>