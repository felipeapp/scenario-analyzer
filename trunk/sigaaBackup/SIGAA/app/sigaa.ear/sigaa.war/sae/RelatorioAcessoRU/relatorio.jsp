<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #DEDFE3; border: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px; }
</style>
<f:view>
	<h:form>
	<h2>Relatório de Acessos das Catracas do RU</h2>

	<c:if test="${empty relatorioAcessoRu.acessosRu}">
	
	<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado de acordo com os critérios de busca informados.</div>
	
	</c:if>
	
	<c:if test="${not empty relatorioAcessoRu.acessosRu}">
		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Período:</th>
					<td> 
						<h:outputText value="#{relatorioAcessoRu.buscaDataInicio}" /> 
					</td>
						
					<td> 
						<h:outputText value="  a  " />
						<h:outputText value="#{relatorioAcessoRu.buscaDataFim}" /> 
					</td>
				</tr>
				
				
			</table>
		</div>
		<br/>
		<c:set var="totalDia" value="0" />
   		
		<table  cellspacing="1" width="100%" style="font-size: 11px;">
			<c:forEach var="acesso" items="#{relatorioAcessoRu.acessosRu}" varStatus="indice">
					<c:set var="diaAtual" value="${acesso.data_acesso_ru}"/>
					
					<tr ><td colspan="3" align="center" style="font-size: 1.1em; font-weight: bold; border-top:2px black solid;"><ufrn:format type="data" valor="${acesso.data_acesso_ru}"/></td></tr>
					<tr class="header">
						<td colspan="3"
							style="background-color: #c0c0c0; border: 1px solid #555;"><b>CAFÉ</b></td>
					</tr>
					<tr class="header">
						<td align="right">Bolsa Residência</td>
						<td align="right">Bolsa Residência Pós</td>
						<td align="right">Bolsa Alimentação</td>
					</tr>
					<tr class="componentes">
						<td align="right">${acesso.cafe_residente_graduacao}</td>
						<td align="right">${acesso.cafe_residente_pos}</td>
						<td align="right">${acesso.cafe_alimentacao}</td>
					</tr>
					<tr class="componentes">
						<td align="right" colspan="2"><b>Total:</b></td>
						<td align="right"><b>${acesso.cafe_residente_graduacao + acesso.cafe_residente_pos + acesso.cafe_alimentacao }</b></td>
						<c:set var="totalDia" value="${totalDia + (acesso.cafe_residente_graduacao + acesso.cafe_residente_pos + acesso.cafe_alimentacao)}"/>
					</tr>
					<tr class="espaco" >
						<td colspan="3">&nbsp;</td>
					</tr>
					
					
					
					<tr class="header">
						<td colspan="3"
							style="background-color: #c0c0c0; border: 1px solid #555;"><b>ALMOÇO</b></td>
					</tr>
					<tr class="header">
						<td align="right">Bolsa Residencia</td>
						<td align="right">Bolsa Residencia Pós</td>
						<td align="right">Bolsa Alimentação</td>
					</tr>
					<tr class="componentes">
						<td align="right">${acesso.almoco_residente_graduacao}</td>
						<td align="right">${acesso.almoco_residente_pos}</td>
						<td align="right">${acesso.almoco_alimentacao}</td>
					</tr>
					<tr class="componentes">
						<td align="right" colspan="2"><b>Total:</b></td>
						<td align="right"><b>${acesso.almoco_residente_graduacao + acesso.almoco_residente_pos + acesso.almoco_alimentacao }</b></td>
						<c:set var="totalDia" value="${totalDia + (acesso.almoco_residente_graduacao + acesso.almoco_residente_pos + acesso.almoco_alimentacao) }"/>
					</tr>
					<tr class="espaco">
						<td colspan="3">&nbsp;</td>
					</tr>
					
					
					<tr class="header">
						<td colspan="3"
							style="background-color: #c0c0c0; border: 1px solid #555;"><b>JANTAR</b></td>
					</tr>
					<tr class="header">
						<td align="right">Bolsa Residencia</td>
						<td align="right">Bolsa Residencia Pós</td>
						<td align="right">Bolsa Alimentação</td>
					</tr>
					<tr class="componentes">
						<td align="right">${acesso.janta_residente_graduacao}</td>
						<td align="right">${acesso.janta_residente_pos}</td>
						<td align="right">${acesso.janta_alimentacao}</td>
					</tr>
					<tr class="componentes">
						<td align="right" colspan="2"><b>Total:</b></td>
						<td align="right"><b>${acesso.janta_residente_graduacao + acesso.janta_residente_pos + acesso.janta_alimentacao }</b></td>
						<c:set var="totalDia" value="${totalDia + (acesso.janta_residente_graduacao + acesso.janta_residente_pos + acesso.janta_alimentacao) }"/>
					</tr>
					<tr class="espaco">
						<td colspan="3">&nbsp;</td>
					</tr>
					

				</c:forEach>
			</table>

		<table align="center">
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
					<h:commandLink value="Total de acessos ao Restaurante Universitário pelas catracas:" action="#{relatorioAcessoRu.relatorioAcessosRuDetalhes}" >
					</h:commandLink>
					${totalDia}
					</td>
				</tr>
			</tfoot>
		</table>

		</c:if>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>