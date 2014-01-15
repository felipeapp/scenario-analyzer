<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<c:set var="confirmRemover" value="if (!confirm('Confirma a remoção desta informação?')) return false" scope="request" />


<h2><ufrn:subSistema> > Campos MARC <c:if test="${! etiquetaMBean.permissaoAlteracaoTotal}"> Locais </c:if></ufrn:subSistema></h2>


<div class="descricaoOperacao"> 
    
     <c:if test="${! etiquetaMBean.permissaoAlteracaoTotal}">  
	     <p>Campos locais são campos de dados MARC cuja tag possui o dígito <strong>9</Strong>.</p>
	     <p>Esses campos não são definidos no padrão MARC, ficando cada biblioteca livre para definir seu uso.</p>
	     <br/>
	     <p>
		     <strong>
		     	OBSERVAÇÃO: Caso um campo local seja removido o sistema não permitirá mais serem adicionados nem editados
		     	Títulos ou Autoridades que possuam esse campo. 
		     </strong>
	     </p>
     </c:if>
     
     <c:if test="${etiquetaMBean.permissaoAlteracaoTotal}">  
	     <p>Caro usuário, esta operação permite alterar o conteúdo de <strong>TODOS</strong> os campos MARC cadastrados no sistema.</p>
	     <p>O conteúdo da maioria desses campos é definido no padrão MARC e, a não ser por um erro na base, os seus valores não precisam ser alterados.</p>
	     <br/>
	     <p>
		     <strong>
		     	OBSERVAÇÃO: Caso um campo seja removido o sistema não permitirá mais serem adicionados nem editados
		     	Títulos ou Autoridades que possuam esse campo. 
		     </strong>
	     </p>
     </c:if>
     
</div>


<f:view>

	
	<a4j:keepAlive beanName="etiquetaMBean"/>
	
	<h:form id="formCadastraEtiqueta">


		<%-- Mostra todas as etiquetas locais cadastradas --%>
	
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
	
			<div class="infoAltRem" style="margin-top:15px">
	
				<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO }  %>">	
					<h:graphicImage value="/img/adicionar.gif" />
					<c:if test="${! etiquetaMBean.permissaoAlteracaoTotal}"> 
						<h:commandLink value="Cadastrar Novo Campo Local" action="#{etiquetaMBean.novaEtiqueta}" />
					</c:if>
					<c:if test="${etiquetaMBean.permissaoAlteracaoTotal}"> 
						<h:commandLink value="Cadastrar Novo Campo" action="#{etiquetaMBean.novaEtiqueta}" />
					</c:if>
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Editar Campo Local
					<h:graphicImage value="/img/garbage.png" style="overflow: visible;" />: Remover Campo Local
				</ufrn:checkRole>
			</div> 
			
		</ufrn:checkRole>



		<table width="100%" class="listagem">
			<caption>Campos MARC  <c:if test="${! etiquetaMBean.permissaoAlteracaoTotal}"> Locais </c:if> Cadastrados no Sistema ( ${fn:length(etiquetaMBean.allEtiquetas)} )</caption>
		
			<thead>
				<tr align="center">
					<th>Tag</th>
					<th>Descrição</th>
					<th width="20%">Tipo</th>
					<th style="text-align:center">Pode Repetir?</th>
					
					<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
						<th> </th>
						<th> </th>
					</ufrn:checkRole>
				</tr>
			</thead>
		
			<tbody>
		
				<c:forEach var="varEtiqueta" items="#{etiquetaMBean.allEtiquetas}" varStatus="loop" >
					
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
						<td>${varEtiqueta.tag} </td>
						<td>${varEtiqueta.descricao} </td>
						
						<c:if test="${varEtiqueta.tipo == varEtiqueta.tipoBibliografica}"> 
							<td style="color: #2B60DE ">
								<c:out value="Bibliográfica"/>
							</td>
						</c:if>
						
						<c:if test="${varEtiqueta.tipo == varEtiqueta.tipoAutoridade}"> 
						 	<td style="color: #7E3817 ">
						 		<c:out value="Autoridade"/>
						 	</td>
						</c:if>
					
		
						<td style="text-align:center">
							<c:if test="${varEtiqueta.repetivel == true}"> 
								<c:out value="Sim"/>
							</c:if>
							<c:if test="${varEtiqueta.repetivel == false}"> 
							 	<c:out value="Não"/>
							</c:if>
						</td>

						<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">

							<c:if test="${varEtiqueta.alteravel || etiquetaMBean.permissaoAlteracaoTotal}">
								<td width="20px"> 
									<h:commandLink action="#{etiquetaMBean.preAtualizar}" title="Clique aqui para alterar este campo" rendered="#{varEtiqueta != null && varEtiqueta.id > 0 }">
				                	    	<h:graphicImage value="/img/alterar.gif"/> 
											<f:param value="#{varEtiqueta.id}" name="idEtiqueta"/>
				                	 </h:commandLink>
								</td>
								
								
							
								<td width="20px"> 
			                		<h:commandLink action="#{etiquetaMBean.remover}" onclick="#{confirmRemover}" title="Clique aqui para remover este campo" rendered="#{varEtiqueta != null && varEtiqueta.id > 0 }">
			                	    	<h:graphicImage value="/img/garbage.png"/> 
										<f:param value="#{varEtiqueta.id}" name="idEtiquetaRemocao"/>
			                	    </h:commandLink>
		                		</td>
		                		
							</c:if>
							
						
						
							<c:if test="${! varEtiqueta.alteravel && ! etiquetaMBean.permissaoAlteracaoTotal}">
								<td ></td>
								<td ></td> 
							</c:if>
						
						</ufrn:checkRole>
						
					</tr>
			
				</c:forEach>
		
			</tbody>
		
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{etiquetaMBean.cancelar}"></h:commandButton>
					</td>
				</tr>
			</tfoot>
		
		</table>



	</h:form>

</f:view>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>