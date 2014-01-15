<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:form id="form">
	
			<h2> <ufrn:subSistema /> &gt; Publica��o de Edital</h2>
			
			<table id="tableEdital" class="visualizacao" width="75%">
		       <caption>Dados do Edital</caption>
		       <tr>
		           <th width="30%" >Ano do Edital:</th>
		           <td>
		           		<h:outputText value="#{editalPesquisaMBean.obj.edital.ano}" />
		           </td>
		       </tr>

		  	   <tr>
		           <th>C�digo:</th>
		           <td>
		           		<h:outputText value="#{editalPesquisaMBean.obj.codigo}" />
		           </td>
		       </tr>
		       
		       <tr>
		           <th width="30%" >Descri��o:</th>
		           <td>
		           		<h:outputText value="#{editalPesquisaMBean.obj.edital.descricao}" />
		           </td>
		       </tr>
		       
		       <tr>
		           <th>Per�odo de Submiss�es:</th>
		           <td>
						<h:outputText value="#{editalPesquisaMBean.obj.edital.inicioSubmissao}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
						</h:outputText> a 
						<h:outputText value="#{editalPesquisaMBean.obj.edital.fimSubmissao}">
							<f:convertDateTime pattern="dd/MM/yyyy"/>
						</h:outputText>
				   </td>
		       </tr>
		       
		       <tr>
		           <th>Titula��o m�nima para solicita��o de cotas:</th>
		           <td>
						<h:outputText value="#{editalPesquisaMBean.obj.titulacaoMinimaCotasDescricao}" />
				   </td>
		       </tr>
		       
		       <tr>
		           <th>Per�odo de Cota:</th>
		           <td>
						<h:outputText value="#{editalPesquisaMBean.obj.cota.descricao}" />
				   </td>
		       </tr>
		       
		       <tr>
		           <th>Categoria:</th>
		           <td>
						<h:outputText value="#{editalPesquisaMBean.obj.categoria.denominacao}" />
				   </td>
		       </tr>
		       
		       <tr>
		           <th>Edital para Volunt�rios:</th>
		           <td>
		           		<c:if test="${editalPesquisaMBean.obj.voluntario}">SIM</c:if>
		           		<c:if test="${!editalPesquisaMBean.obj.voluntario}">N�O</c:if>
				   </td>
		       </tr>
		       
		       <tr>
		           <th>Avalia��o Vigente:</th>
		           <td>
						<c:if test="${editalPesquisaMBean.obj.avaliacaoVigente}">SIM</c:if>
		           		<c:if test="${!editalPesquisaMBean.obj.avaliacaoVigente}">N�O</c:if>
				   </td>
		       </tr>
		       
		       <c:if test="${!editalPesquisaMBean.obj.distribuicaoCotas}">
		       		<tr>
			           <th>Distribui��o de Cotas de Bolsas:</th>
			           <td>
			           		<c:if test="${!editalPesquisaMBean.obj.distribuicaoCotas}">N�O</c:if>
					   </td>
			       </tr>
		       </c:if>
		       
		       <c:if test="${editalPesquisaMBean.obj.distribuicaoCotas}">
					<tr>
						<td colspan="2" class="subFormulario"> Par�metros da Distribui��o de Cotas </td>
					</tr>
					
					<tr>
						<th> FPPI M�nimo: </th>
						<td>
							<h:outputText value="#{editalPesquisaMBean.obj.fppiMinimo}" />
						</td>
					</tr>
					
					<tr>
			           <th>Divulgar Resultado:</th>
			           <td>
							<c:if test="${editalPesquisaMBean.obj.resultadoDivulgado}">SIM</c:if>
			           		<c:if test="${!editalPesquisaMBean.obj.resultadoDivulgado}">N�O</c:if>
					   </td>
			        </tr>
					
					<c:if test="${not empty editalPesquisaMBean.obj.cotas}">
						<tr>
							<td colspan="2" class="subFormulario"> Cotas distribu�das </td>
						</tr>
						<tr><td colspan="2">
							<table id="tableCotas" class="listagem">
				        		<thead>
				        			<tr>
				        				<th style="text-align: left">Tipo da bolsa</th>
				        				<th style="text-align: right">Quantidade</th>
				        			</tr>
				        		</thead>
				        		<c:set var="totalCotas" value="0"/>
				        		<c:forEach items="${editalPesquisaMBean.obj.cotas}" var="cota" varStatus="row">
				        			<tr>
				        				<td>${cota.tipoBolsa.descricaoResumida}</td>
				        				<td align="right">${cota.quantidade}</td>
				        				<c:set var="totalCotas" value="${totalCotas + cota.quantidade}"/>
				        			</tr>
				        		</c:forEach>
				        		<tfoot>
				        			<tr>
				        				<th style="text-align: left">Total</th>
				        				<th style="text-align: right">${totalCotas}</th>
				        			</tr>
				        		</tfoot>
				        	</table>
						</td></tr>
					</c:if>
				</c:if>
				<tfoot>
					<tr>
						<td colspan="2">
							<c:choose>
								<c:when test="${editalPesquisaMBean.confirmButton != 'Remover'}">
									<center><h:commandLink value="<<Voltar" action="#{editalPesquisaMBean.iniciar}" /> </center> 
								</c:when>
								<c:otherwise>
									<h:commandButton value="Voltar" action="#{editalPesquisaMBean.iniciar}" />
									<h:commandButton value="Remover" action="#{editalPesquisaMBean.remover}" />
									<h:commandButton value="Cancelar" action="#{editalPesquisaMBean.cancelar}" immediate="true" onclick="#{confirm}"/>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</tfoot>
			</table>
			
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>