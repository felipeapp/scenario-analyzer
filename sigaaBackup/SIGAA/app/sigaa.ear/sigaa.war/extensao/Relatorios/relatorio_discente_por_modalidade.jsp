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

	<h2><ufrn:subSistema /> > Relat�rio Nominal do Total de Discentes de Extens�o por Tipo</h2>
	
	
	
	
	<h:form id="form">
	
		<div class="descricaoOperacao">
			<p>Senhor(a) usu�rio(a), <br/><br/>
			
				Este � o relat�rio nominal do Total de Discentes de Extens�o por tipo de V�nculo. Com este relat�rio o(a) senhor(a) pode visualizar <br/>
				o total de discentes Volunt�rios, Bolsista Faex, Em atividade Currivular e Bolsista Externo presentes por A��es de Extens�o, bem como o total de Discentes <br/>
				de cada A��o Extensionista. Esta disposto tamb�m um resumo(no final da p�gina) contendo o total de Volunt�rios, Bolsista Faex, Em atividade Currivular e Bolsista Externo<br/>
				de todas as A��es Extensionistas, como tamb�m o total de discentes de todas as A��es.<br><br/>						
			
			
				<b>Aten��o</b><br/>
				Alguns Discentes podem ter o nome na cor Azul, o que significa que tais discentes s�o repetidos, ou seja, s�o discentes<br/>
				de mais de uma a��o ou est�o em uma mesma a��o mais de uma vez, exercendo fun��es diferentes.<br/>
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
					<h:commandButton value="Gerar Relat�rio" action="#{ relatoriosAtividades.gerarRelatorioDiscentePorModalidade }"
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
		        	<th ></th>
		        	
		        	
		        </tr>
		 	</thead>		 	
		 	<tbody>	
		 	
		 	<c:set var="TOTAL"  value="0"/>	 	
		 		 	
	       	<c:set var="TOTAL_VOLUNTARIO"  value="0"/>
		 	<c:set var="TOTAL_FAEX" value="0"/>
		 	<c:set var="TOTAL_CURRICULAR" value="0"/>
		 	<c:set var="TOTAL_EXTERNO"  value="0"/>
		 	
		 	<c:set var="VOLUNTARIO"  value="1"/>
		 	<c:set var="FAEX"  value="2"/>
		 	<c:set var="CURRICULAR"  value="3"/>
		 	<c:set var="EXTERNO"  value="4"/>
		 	
		 	
		 	
		 	
	       	
	       	<c:forEach items="#{relatoriosAtividades.atividadesLocalizadas}" var="ativ" varStatus="status">  			
		 			
		 			<c:set var="TOTAL_VOLUNTARIO_ACAO"  value="0"/>
		 			<c:set var="TOTAL_FAEX_ACAO" value="0"/>
		 			<c:set var="TOTAL_CURRICULAR_ACAO" value="0"/>
		 			<c:set var="TOTAL_EXTERNO_ACAO"  value="0"/>
	       	 		
	               <tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
	                    <td > ${ativ.codigo}</td>
	                    <td width="32%"> ${ativ.projeto.titulo}</td>
	                    <td></td>                 
	                    <td width="2%">					
							<h:commandLink title="Visualizar A��o de Extens�o" action="#{atividadeExtensao.view}" id="visualizarAcaooExtensao">
							    <f:param name="id" value="#{ativ.id}"/>
			                	<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						</td>
					</tr>
						
					<c:set var="TAMANHO_EQUIPE_ACAO" value="${ fn:length(ativ.discentesSelecionados) }" />					
					
					<c:forEach items="#{ativ.discentesSelecionados}" var="de" varStatus="loop">																			
							
							<c:if test="${  de.tipoVinculo.id == VOLUNTARIO }">														
								<c:set var="TOTAL_VOLUNTARIO_ACAO"  value="${ TOTAL_VOLUNTARIO_ACAO + 1 }"/>										
							</c:if>
							
							<c:if test="${  de.tipoVinculo.id == FAEX }">														
								<c:set var="TOTAL_FAEX_ACAO"  value="${ TOTAL_FAEX_ACAO + 1 }"/>
							</c:if>
							
							<c:if test="${  de.tipoVinculo.id == CURRICULAR }">
								<c:set var="TOTAL_CURRICULAR_ACAO"  value="${ TOTAL_CURRICULAR_ACAO + 1 }"/>		
							</c:if>
							
							<c:if test="${  de.tipoVinculo.id == EXTERNO }">																					
								<c:set var="TOTAL_EXTERNO_ACAO"  value="${ TOTAL_EXTERNO_ACAO + 1 }"/>								
							</c:if>
							
												
							<tr>									
								<c:if test="${de.selecionado}">
									<td width="40%"> <font color="blue">${de.discente.pessoa.nome}</font> </td>
								</c:if>									
									
								<c:if test="${!de.selecionado}">
									<td width="40%"> ${de.discente.pessoa.nome} </td>
								</c:if>						
								<td></td>
								<td>${de.tipoVinculo.descricao}</td>				
								<td></td>									
							</tr>					
																														
																			
						
						<%-- �LTIMO DISCENTE DESTA A��O --%>
						<c:if test="${loop.index == TAMANHO_EQUIPE_ACAO - 1 }">						
							<tr>
								<td colspan="4">
									<br/> 
									<br/>
									<table class="subFormulario" width="50%">
										<caption style="text-align: center">Resumo desta A��o</caption>
								
										<tr>
											
											<td><b>Total de Discentes Volunt�rios:</b></td>
											<td><b>${TOTAL_VOLUNTARIO_ACAO}</b></td>
										</tr>
									
										<tr>
											<td><b>Total de Discentes Bolsista Faex:</b></td>
											<td><b>${TOTAL_FAEX_ACAO}</b></td>
										</tr>
									
										<tr>
											<td><b>Total de Discentes em Atividade Curricular:</b></td>
											<td><b>${TOTAL_CURRICULAR_ACAO}</b></td>
										</tr>
									
										<tr>
											<td><b>Total de Discentes Externo:</b></td>
											<td><b>${TOTAL_EXTERNO_ACAO}</b></td>
										</tr>	
										
										<tr>
											<td><b>Total de Discentes da A��o:</b></td>
											<td><b>${TOTAL_VOLUNTARIO_ACAO + TOTAL_FAEX_ACAO + TOTAL_CURRICULAR_ACAO + TOTAL_EXTERNO_ACAO}</b></td>
										</tr>
																		
									</table>
									<br/>
									<br/>
									
							</tr>						
						</c:if>		 											              
	          </c:forEach>	   
	          			<c:set var="TOTAL_VOLUNTARIO"  value="${ TOTAL_VOLUNTARIO + TOTAL_VOLUNTARIO_ACAO }"/>
		 				<c:set var="TOTAL_FAEX" value="${ TOTAL_FAEX + TOTAL_FAEX_ACAO }"/>
		 				<c:set var="TOTAL_CURRICULAR" value="${ TOTAL_CURRICULAR + TOTAL_CURRICULAR_ACAO }"/>
		 				<c:set var="TOTAL_EXTERNO"  value="${ TOTAL_EXTERNO + TOTAL_EXTERNO_ACAO }"/>       
	       </c:forEach>   
	          <c:set var="TOTAL"  value="${TOTAL_VOLUNTARIO + TOTAL_FAEX + TOTAL_CURRICULAR + TOTAL_EXTERNO}"/>
	          
		 	</tbody>
		 </table>
		 
		 <br/>
		 <br/>
		 
		 <table class="subFormulario" width="40%">
		    <caption style="text-align: center"> Resumo Geral</caption>
		    
		    <tr> 
		    	<td> <b> Total de Discentes Volunt�rios das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_VOLUNTARIO} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de Discentes Bolsista Faex das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_FAEX} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de Discentes em Atividade Curricular das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_CURRICULAR} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de Discentes Externos das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL_EXTERNO} </b> </td>		    
		    </tr>
		    
		    <tr> 
		    	<td> <b> Total de Discentes das a��es: </b>  <td>
		    	<td align="right"> <b> ${TOTAL} </b> </td>		    
		    </tr>
		    
		 </table>
		 <br/>
		 		 
		 </c:if>
		
		
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>