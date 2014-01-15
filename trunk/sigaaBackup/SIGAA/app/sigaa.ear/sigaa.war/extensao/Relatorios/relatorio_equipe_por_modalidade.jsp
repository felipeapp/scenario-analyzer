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
			<p>Senhor(a) usu�rio(a), <br/><br/>
			
				Este � o relat�rio nominal do Total de Membros por tipo(Docente,Discente,Servidor,Externo). Com este relat�rio o(a) senhor(a) pode visualizar <br/>
				o total de membros Discentes, Docentes, Servidores e Externos presentes por A��es de Extens�o, bem como o total de membros <br/>
				de cada A��o Extensionista. Esta disposto tamb�m um resumo(no final da p�gina) contendo o total de Discentes, Docentes, Servidores<br/>
				e Externos de todas as A��es Extensionistas, como tamb�m o total de membros de todas as A��es.<br><br/>						
			
			
				<b>Aten��o</b><br/>
				Alguns Membros podem ter o nome na cor Azul, o que significa que tais Membros s�o repetidos, ou seja, s�o membros<br/>
				de mais de uma a��o ou est� em uma mesma a��o mais de uma vez, exercendo fun��es diferentes.<br/>
			</p>
		
		</div>	

		<table class="formulario" width="80%">
			<caption>Consultar Relat�rio Gerais de Extens�o</caption>
			<tbody>			

				
				<tr>
					<th class="required">Tipo da A��o:</th>
					<td>
					 	<h:selectOneMenu id="buscaTipoAcao" value="#{relatoriosAtividades.tipoAtividadeExtensao.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
					 		<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
				    	</h:selectOneMenu>	    	 
					</td>
				</tr>		


				<tr>
			    	<th class="required"> <label for="situacaoAcao"> Situa��o da A��o: </label> </th>
			    	<td>
				    	 <h:selectOneMenu  id="buscaSituacao" value="#{relatoriosAtividades.situacaoAtividade.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				    	 	<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcaoExtensaoValidas}" />
			 			 </h:selectOneMenu>
			    	 
			    	</td>
			    </tr>


				<tr>
					<th class="required">Per�odo de execu��o da a��o:</th>
					<td>
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatoriosAtividades.dataInicio}" id="dataInicio" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
						a
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatoriosAtividades.dataFim}" id="dataFim" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
					</td>
				</tr>
		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Gerar Relat�rio" action="#{ relatoriosAtividades.gerarRelatorioEquipePorModalidade }"
							id="BotaoRelatorio" /> 
					<h:commandButton value="Cancelar" action="#{ relatoriosAtividades.cancelar }"
							id="BotaoCancelar" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
		

		<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> </center>		
		
		
		<c:if test="${not empty relatoriosAtividades.atividadesLocalizadas}">
		
		<br/>
		<br/>
		
		<div class="infoAltRem">			
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar A��o de Extens�o					    		    
		</div>
		<br/>
		
		<table class="listagem">
		    <caption> A��es Encontradas ( ${ fn:length(relatoriosAtividades.atividadesLocalizadas) } ) </caption>
	
		      <thead>
		      	<tr>
					<th >Nome</th>
					<th ></th>		        		        		
		        	<th >Categoria</th>
		        	<th >Fun��o</th>
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
							<h:commandLink title="Visualizar A��o de Extens�o" action="#{atividadeExtensao.view}" id="visualizarAcaooExtensao">
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
																														
																			
						
						<%-- �LTIMO MEMBRO DESTA A��O --%>
						<c:if test="${loop.index == TAMANHO_EQUIPE_ACAO - 1 }">						
							<tr>
								<td colspan="4">
									<br/> 
									<br/>
									<table class="subFormulario" width="50%">
										<caption style="text-align: center">Resumo desta A��o</caption>
								
										<tr>
											
											<td><b>Total de Membros Docentes desta A��o:</b></td>
											<td><b>${TOTAL_DOCENTE_ACAO}</b></td>
										</tr>
									
										<tr>
											<td><b>Total de Membros Discentes desta A��o:</b></td>
											<td><b>${TOTAL_DISCENTE_ACAO}</b></td>
										</tr>
									
										<tr>
											<td><b>Total de Membros Servidores desta A��o:</b></td>
											<td><b>${TOTAL_SERVIDOR_ACAO}</b></td>
										</tr>
									
										<tr>
											<td><b>Total de Membros Externos desta A��o:</b></td>
											<td><b>${TOTAL_EXTERNO_ACAO}</b></td>
										</tr>	
										
										<tr>
											<td><b>Total de Membros da A��o:</b></td>
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
		    	<td> <b> Total de membros docentes das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_DOCENTE} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de membros discentes das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_DISCENTE} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de membros servidores das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_SERVIDOR} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de membros externos das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_EXTERNO} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de membros das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL} </b> </td>		    
		    </tr>
		    
		 </table>
		 <br/>
		 		 
		 </c:if>
		
		
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>