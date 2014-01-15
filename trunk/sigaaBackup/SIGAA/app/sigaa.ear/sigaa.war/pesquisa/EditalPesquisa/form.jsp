<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Edital de Pesquisa</h2>

	<h:form id="form">
		<h:outputText value="#{editalPesquisaMBean.create}" />
		
		<table id="tableEdital" class="formulario" width="80%">
			 <caption>Cadastrar Edital</caption>
			 
			 <tr>
	            <th class="required">Ano do Edital:</th>
	            <td>
	            	<h:inputText id="anoEdital" size="4" maxlength="4" value="#{editalPesquisaMBean.obj.edital.ano}" onkeyup="formatarInteiro(this)" />
	            </td>
	        </tr>
			 
			<tr>
	            <th>Código:</th>
	            <td>
	            	<h:inputText id="codigoEdital" size="20" maxlength="50" value="#{editalPesquisaMBean.obj.codigo}" />
	            </td>
	        </tr>

			<tr>
	            <th class="required">Descrição:</th>
	            <td>
	            	<h:inputText id="descricaoEdital" size="60" maxlength="255" value="#{editalPesquisaMBean.obj.edital.descricao}" />
	            </td>
	        </tr>
	        
	        <tr>
	            <th class="required">Período de Submissões:</th>
	            <td>
	            	<t:inputCalendar value="#{editalPesquisaMBean.obj.edital.inicioSubmissao}" size="10" maxlength="10" 
					disabled="#{petBean.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="inicioSubmissao">
					<f:converter converterId="convertData"/> </t:inputCalendar> a 
					<t:inputCalendar value="#{editalPesquisaMBean.obj.edital.fimSubmissao}" size="10" maxlength="10" 
					disabled="#{petBean.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="fimSubmissao">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
	            <td>
           			
	            </td>
	        </tr>

	        <tr>
	            <th class="required">Período de Execução do Projeto:</th>
	            <td>
	            	<t:inputCalendar id="dataInicio" value="#{editalPesquisaMBean.obj.inicioExecucaoProjetos}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é"
						displayValueOnly="#{editalPesquisaMBean.readOnly}">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
					a 
					<t:inputCalendar value="#{editalPesquisaMBean.obj.fimExecucaoProjetos}" size="10" maxlength="10" 
					disabled="#{editalPesquisaMBean.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="fimExecucao">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
	            <td>
           			
	            </td>
	        </tr>
	        
	       	<tr>
	            <th class="required">Titulação mínima para a solicitação de cotas:</th>
	            <td>
	                <h:selectOneMenu id="titulacao" value="#{editalPesquisaMBean.obj.titulacaoMinimaCotas}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItems value="#{editalPesquisaMBean.titulacoes}"/>
					</h:selectOneMenu>
	            </td>
	        </tr>
	        
	        <tr>
	            <th class="required">Período de Cota:</th>
	            <td>
					<h:selectOneMenu id="cota" value="#{editalPesquisaMBean.obj.cota.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItems value="#{editalPesquisaMBean.cotas}"/>
					</h:selectOneMenu>
	            </td>
	        </tr>

	        <tr>
	            <th class="required">Categoria:</th>
	            <td>
	                <h:selectOneMenu id="categoria" value="#{editalPesquisaMBean.obj.categoria.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItems value="#{editalPesquisaMBean.categorias}"/>
					</h:selectOneMenu>
	            </td>
	        </tr>
	        
	        <tr>
	            <th>Validar índice mínimo:</th>
	            <td>
	            
	            	<h:selectOneMenu id="indices" value="#{editalPesquisaMBean.obj.indiceChecagem.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItems value="#{indiceAcademicoMBean.allCombo}"/>
					</h:selectOneMenu>
	            	
	            	Valor Mínimo
	            	<h:inputText id="valorMinimoIndice" value="#{editalPesquisaMBean.obj.valorMinimoIndiceChecagem}" size="10" maxlength="10" 
	            		onkeypress="return(formatarMascara(this,event,'#####.####'))" />
					<ufrn:help>Entre com o valor mínimo do índice selecionado que o aluno deve ter para poder assumir alguma bolsa associada a este edital. Caso não haja restrição deixe ambos os campos em branco.</ufrn:help>
	            </td>
	        </tr>
	        
	        <tr>
	        	<th class="required">Edital para Voluntários?</th>
	        	<td>
					<h:selectOneRadio id="radioVolutarios" value="#{editalPesquisaMBean.obj.voluntario}" > 
						<f:selectItems value="#{editalPesquisaMBean.simNao}" />
					</h:selectOneRadio>								
				</td>
	        </tr>
	        
	        <tr>
	        	<th class="required">Avaliação Vigente?</th>
	        	<td>
					<h:selectOneRadio id="radioAvaliacaoVigente" value="#{editalPesquisaMBean.obj.avaliacaoVigente}" > 
						<f:selectItems value="#{editalPesquisaMBean.simNao}" />
					</h:selectOneRadio>								
				</td>
	        </tr>
	        
	        <tr>
	        	<th class="required">Apenas Coordenador Orienta Plano</th>
		        	<td>
						<h:selectOneRadio id="radioApenasCoordenadorOrientaPlano" value="#{editalPesquisaMBean.obj.apenasCoordenadorOrientaPlano}" style="display: inline"> 
							<f:selectItems value="#{editalPesquisaMBean.simNao}" />
						</h:selectOneRadio>					
					<ufrn:help>Marque sim nesta opção caso apenas o coordenador do projeto possa ser orientador dos planos de trabalho associados a este edital.</ufrn:help>
					</td>
	        </tr>
	        
	        
	        <tr>
	        	<th class="required">Apenas Colaborador Voluntário Cadastra Projeto</th>
		        	<td>
						<h:selectOneRadio id="radioApenasColaboradorVoluntarioCadastraProjeto" value="#{editalPesquisaMBean.obj.apenasColaboradorVoluntarioCadastraProjeto}" style="display: inline"> 
							<f:selectItems value="#{editalPesquisaMBean.simNao}" />
						</h:selectOneRadio>					
					<ufrn:help>Marque sim nesta opção caso apenas docentes externos do tipo colaborador voluntários possa cadastrar projetos associados a este edital. Marque não caso qualquer tipo de docente externo possa cadastrar projetos associados a este edital.</ufrn:help>
					</td>
	        </tr>

	        <tr>
	        	<th class="required">Professor Substituto Cadastra Projeto</th>
		        	<td>
						<h:selectOneRadio id="radioProfessorSubstitutoCadastraProjeto" value="#{editalPesquisaMBean.obj.professorSubstitutoCadastraProjeto}" style="display: inline"> 
							<f:selectItems value="#{editalPesquisaMBean.simNao}" />
						</h:selectOneRadio> 
						<ufrn:help>Marque sim nesta opção caso professores substitutos possam cadastrar projetos vinculados a este edital.</ufrn:help>					
					</td>
	        </tr>
	        
	        
			<tr>
	        	<th class="required">Distribuição de Cotas de Bolsas?</th>
		        	<td>
						<h:selectOneRadio id="radioCotasBolsa" value="#{editalPesquisaMBean.obj.distribuicaoCotas}" onclick="submit()" immediate="true" style="display: inline"> 
							<f:selectItems value="#{editalPesquisaMBean.simNao}" />
							<a4j:support event="onclick" reRender="distribuicaoCota" immediate="true" />
						</h:selectOneRadio>
						<ufrn:help>Define se o edital irá realizar distribuição de cotas de bolsas</ufrn:help>					
					</td>
	        </tr>
	        
	        <t:div id="distribuicaoCota" rendered="#{editalPesquisaMBean.obj.distribuicaoCotas}">
	        
	        	<tr>
					<td colspan="2" class="subFormulario"> Parâmetros da Distribuição de Cotas </td>
				</tr>
				
		        <tr>
		        	<th class="required">FPPI Mínimo:</th>
		        	<td>
		            	<h:inputText id="fppiMinimo" size="5" maxlength="5" value="#{editalPesquisaMBean.obj.fppiMinimo}" onkeydown="return(formataValor(this, event, 2))" converter="convertNota" />
		            	<ufrn:help>Fator de Produtividade em Pesquisa Individual</ufrn:help>
		            </td>
		        </tr>
		        
		        <tr>
		        	<th>Divulgar Resultado?</th>
		        	<td>
						<h:selectOneRadio id="radioResultado" value="#{editalPesquisaMBean.obj.resultadoDivulgado}" > 
							<f:selectItems value="#{editalPesquisaMBean.simNao}" />
						</h:selectOneRadio>								
					</td>
		        </tr>
	        
		        <tr>
		        	<th class="required">Tipo da bolsa:</th>
		        	<td>
		                <h:selectOneMenu id="tipoBolsa" value="#{editalPesquisaMBean.tipoBolsa.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
							<f:selectItems value="#{editalPesquisaMBean.tiposBolsa}"/>
						</h:selectOneMenu>
		            </td>
		        </tr>
		        
		        <tr>
		        	<th class="required">Quantidade:</th>
		        	<td>
		            	<h:inputText id="quantidade" size="5" maxlength="3" value="#{editalPesquisaMBean.quantidade}" onkeyup="formatarInteiro(this)" />
		            </td>
		        </tr>
		        
		        <tr>
		        	<td colspan="2" align="center">
		        		<h:commandButton id="adicionarCota" value="Adicionar" action="#{editalPesquisaMBean.adicionarCotas}" />
		        	</td>
		        </tr>

		        <tr>
		        	<td colspan="3" align="center">
		        		
		        		<c:if test="${not empty editalPesquisaMBean.obj.cotas}">
		        			
							<br/>
							<div class="infoAltRem">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover cota
							</div>
							
							<table class="subFormulario" width="100%">
								<caption>Cotas</caption>
								<tr><td>
									<t:dataTable id="tableCotas" value="#{editalPesquisaMBean.obj.cotas}" var="cota" styleClass="listagem" width="100%;">
																		
										<t:column>
											<f:facet name="header">
												<f:verbatim>Tipo da bolsa</f:verbatim>
											</f:facet>
											<h:outputText value="#{cota.tipoBolsa.descricaoResumida}" />
										</t:column>
										
										<t:column>
											<f:facet name="header">
												<f:verbatim>Quantidade</f:verbatim>
											</f:facet>
											<c:choose>
												<c:when test="${cota.id != 0}">
													<h:inputText size="5" maxlength="3" onchange="submit()" value="#{cota.quantidade}" />
												</c:when>
												<c:otherwise>
													<h:outputText value="#{cota.quantidade}" />
												</c:otherwise>
											</c:choose>
										</t:column>
										
										<t:column width="15" styleClass="centerAlign">
											<h:commandLink title="Remover cota" action="#{ editalPesquisaMBean.removerCotas }" immediate="true">
										        <f:param name="idCotas" value="#{cota.id}"/>
										        <f:param name="descricao" value="#{cota.tipoBolsa.descricaoResumida}"/>
									    		<h:graphicImage url="/img/delete.gif" />
											</h:commandLink>
										</t:column>
											
										</t:dataTable>
								</td></tr>
							</table>
							
				       	</c:if>
		        	</td>
		        </tr>
		        
	        </t:div>
        
        	<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="cadastrar" value="#{editalPesquisaMBean.confirmButton}" action="#{editalPesquisaMBean.cadastrar}" />
					 	<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{editalPesquisaMBean.cancelar}" />
					 </td>
				</tr>
			</tfoot>
		
		</table>
		
		
	
	</h:form>
	
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>