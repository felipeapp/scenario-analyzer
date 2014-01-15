<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>

	.nomeAcao {
		font-weight: bold;
	}
	
	.detalhesAcao td {
		padding-top: 12px;
	}
	
	.totaisAcao td{
	}
	
	.totaisUnidade td {
		background: #EEE;
		font-variant: small-caps;
		padding:  3px;		
	}

	.linhaTotais td {
		margin-top: 5px;
		border-top: 1px solid #999;
	}
	
	table.relatorio tfoot td {
		background: #DDD;
		font-weight: bold;
		font-variant: small-caps;
		font-size: 1.2em;
		padding: 3px;
	} 

</style>

<f:view>

	<h:outputText value="#{relatoriosAtividades.create}"/>
	<h2>Relatório para Descentralização de Recursos do FAEx</h2>
	<br/>

	<c:set var="result" value="${relatoriosAtividades.atividadesLocalizadas}"/>

	<c:if test="${empty result}">
		<center><i> Nenhuma ação de extensão localizada </i></center>
	</c:if>

	<c:if test="${not empty result}">
	
	<div id="parametrosRelatorio">
	   <table >
			<tr>
				<th>Edital:</th>
				<td> ${relatoriosAtividades.edital.descricao == null ? 'TODOS' : relatoriosAtividades.edital.descricao}</td>
			</tr>
			<tr>
				<th>Área Temática:</th>
				<td> 
					${relatoriosAtividades.areaTematica.descricao == null ? 'TODAS' : relatoriosAtividades.areaTematica.descricao} 
				</td>
			</tr>
			<tr>
				<th>Unidade Responsável:</th>
				<td> ${relatoriosAtividades.unidade.nome == null ? 'TODAS' : relatoriosAtividades.unidade.codigoNome}</td>
			</tr>
			<tr>
                <th>Dimensão Acadêmica:</th>
                <td> ${relatoriosAtividades.buscaAcoesAssociadas == null ? 'TODAS' : ((relatoriosAtividades.buscaAcoesAssociadas) ? 'AÇÕES ASSOCIADAS' : 'AÇÕES ISOLADAS')}</td>
            </tr>
		</table>
	</div>
	<br />
		 <table class="tabelaRelatorio" width="100%" id="lista">
		 	<tbody>
				 <c:set value="0" var="total_geral_recursos"/>		 	
				 <c:set value="0" var="total_geral_bolsas"/>				 				 
				 <c:set value="0" var="id_unidade"/>
				 <c:set value="0" var="total_unidade_proponente_recursos"/>
				 <c:set value="0" var="total_unidade_proponente_bolsas"/>
				 
	    		 <c:forEach items="${result}" var="atv" varStatus="status">

					<c:if test="${atv.unidade.id != id_unidade}">
					 	<c:set value="${atv.unidade.id}" var="id_unidade"/>
					 	<c:set value="1" var="num_acoes_unidade"/>

			           <c:if test="${total_unidade_proponente_recursos != 0}">
				  			  <tr class="totaisUnidade linhaTotais">
				                    <td colspan="4">Total de Recursos da Unidade </td>
				                    <td align="right"><b>R$	<fmt:formatNumber value="${total_unidade_proponente_recursos}" pattern="#,##0.00" type="currency"/></b></td>
				              </tr>
				              <tr class="totaisUnidade">
				                    <td colspan="4">Total de Bolsas da Unidade</td>
				                    <td align="right"><b><fmt:formatNumber value="${total_unidade_proponente_bolsas}" pattern="##0"/></b></td>
				              </tr>				              
					  		  <tr><td colspan="5"><br/></td></tr>
						</c:if>

					 	<c:set value="0" var="total_unidade_proponente_recursos"/>
					 	<c:set value="0" var="total_unidade_proponente_bolsas"/>
			            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			                  <td colspan="5" class="subFormulario"> ${atv.unidade.codigoNome}</td>
			            </tr>
			        </c:if>
			        
			         <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" } detalhesAcao">
			         		<td width="1%" valign="top">${num_acoes_unidade}.</td>
		                    <td  colspan="4" width="40%"> 
		                    	<span class="nomeAcao"> ${atv.codigoTitulo} </span> 
			                     <br/><i>Coordenador(a):</i> ${atv.coordenacao.pessoa.nome}
			                     <br/><i>Área Temática:</i> ${atv.areaTematicaPrincipal.descricao}		                    
		                    </td>
		             </tr>
	              
						<c:set value="0" var="total_acao"/>
		              	<c:forEach items="${atv.orcamentosConsolidados}" var="orc" varStatus="status">
		              		  <c:set value="${total_acao + orc.fundoConcedido}" var="total_acao"/>
				              <tr class="orcamentoProjeto">
									<td></td>
				                    <td></td>
				                    <td></td>
				                    <td>
				                    	<c:if test="${empty orc.elementoDespesa.descricao}">
				                    		<i>Orçamento não informado.</i>
				                    	</c:if> 
				                    	${orc.elementoDespesa.descricao}
				                    </td>
				                    <td align="right">
				                    	<c:if test="${not empty orc.elementoDespesa.descricao}">
				                    		R$	<fmt:formatNumber value="${orc.fundoConcedido}" pattern="#,##0.00" type="currency" />
				                    	</c:if>
				                    </td>
				              </tr>
		                </c:forEach>

						  <tr class="totaisAcao">
 			                    <td></td>
								<td></td>
			                    <td></td>
			                    <td><b><i>Total de Recursos</i></b></td>
			                    <td align="right"><b><i>R$	<fmt:formatNumber value="${total_acao}" pattern="#,##0.00" type="currency"/></i></b></td>
		    	          </tr>
		        	      <tr class="totaisAcao">
			                    <td></td>
			                    <td></td>
			                    <td></td>
		                    	<td><b><i>Total de Bolsas</i></b></td>
		                    	<td align="right"><b><i><fmt:formatNumber value="${atv.bolsasConcedidas}" pattern="##0"/></i></b></td>
		            	  </tr>

			            <c:set value="${num_acoes_unidade + 1}" var="num_acoes_unidade"/> 
			  			<c:set value="${total_geral_recursos + total_acao}" var="total_geral_recursos"/>
			  			<c:set value="${total_geral_bolsas + atv.bolsasConcedidas}" var="total_geral_bolsas"/>

			  			<c:set value="${total_unidade_proponente_recursos + total_acao}" var="total_unidade_proponente_recursos"/>
			  			<c:set value="${total_unidade_proponente_bolsas + atv.bolsasConcedidas}" var="total_unidade_proponente_bolsas"/>
			  			
	          </c:forEach>
		           <c:if test="${total_unidade_proponente_recursos != 0}">
			  			  <tr class="totaisUnidade linhaTotais">
			                    <td colspan="4">Total de Recursos da Unidade </td>
			                    <td align="right"><b>R$	<fmt:formatNumber value="${total_unidade_proponente_recursos}" pattern="#,##0.00" type="currency"/></b></td>
			              </tr>
			              <tr class="totaisUnidade">
			                    <td colspan="4">Total de Bolsas da Unidade</td>
			                    <td align="right"><b><fmt:formatNumber value="${total_unidade_proponente_bolsas}" pattern="##0"/></b></td>
			              </tr>				              
				  		  <tr><td colspan="5"><br/></td></tr>
					</c:if>
		 	</tbody>

			<tfoot>
				<tr>
					<td colspan="5">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="4">Total de Ações</td>
					<td align="right"> ${fn:length(result)}</td>
				</tr>  
		 	    <tr>
	            	<td colspan="4">Total Geral de Recursos</td>
	                <td align="right">R$ <fmt:formatNumber value="${total_geral_recursos}" pattern="#,##0.00" type="currency"/></td>
	            </tr>
	            <tr>
	              	<td colspan="4">Total Geral de Bolsas </td>
	            	<td align="right"><fmt:formatNumber value="${total_geral_bolsas}" pattern="##0"/></td>
	            </tr>	    	            
			</tfoot>	 	
		 	
		 </table>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>