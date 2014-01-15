<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Visualização de Resumo das Atividades do Projeto (SID) </h2>

		<h:outputText value="#{resumoSid.create}" />
	
		<table class="tabelaRelatorio" width="100%">
		<caption class="listagem"> Resumo para o Seminário de Inicação à Docência </caption>
	
		<tr>
			<th width="25%">
			 	<b>Projeto:</b>
			</th>	
			<td>
				<h:outputText value="#{resumoSid.obj.projetoEnsino.anoTitulo}" />
			</td>
		</tr>
	

		<tr>
			<th>
			 	<b>Coordenador(a):</b>
			</th>	
			<td>
				<h:outputText value="#{resumoSid.obj.projetoEnsino.coordenacao.pessoa.nome}" />
			</td>
		</tr>
		
		<tr>
			<th>
				<b>Ano do Seminário:</b><br/>
			</th>
			<td>	
				<h:outputText id="txtAnoReferencia" value="#{resumoSid.obj.anoSid}"/>
			</td>
		</tr>

		<tr>
			<th>
				<b>Situação do Resumo:</b><br/>
			</th>
			<td>	
				<i><h:outputText value="#{resumoSid.obj.status.descricao}"/></i>
			</td>
		</tr>

		
		<tr>
			<th>
				<b>Data de Envio:</b><br/>
			</th>
			<td>	
				<i><fmt:formatDate value="${resumoSid.obj.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss"/></i>
			</td>
		</tr>
		
		
		<tr>
			<td colspan="2" class="subFormulario"> Resumo </td>
		</tr>
		
		
		<tr>
			<td align="justify" colspan="2"><h:outputText  id="txaResumo"  value="#{resumoSid.obj.resumo}"/></td>
		</tr>
		
		
		<tr>
			<td colspan="2">
				<i>
				<b>Palavras Chave:</b>
				<h:outputText id="txtPalavrasChave" value="#{resumoSid.obj.palavrasChave}"/> </td>
				</i>
			</td>
		</tr>
		
		
		<tr>
			<td colspan="2" class="subFormulario"> Lista de discentes que participaram da elaboração do Resumo do SID. </td>
		</tr>
		
		
		<tr>
			<td colspan="2">
					<t:dataTable value="#{resumoSid.obj.participacoesSid}" var="partSid" rowClasses="linhaPar,linhaImpar" width="100%" id="psid">						
						
						
						
						<t:column>
								<h:outputText value="<b>#{partSid.discenteMonitoria.discente.matriculaNome}</b>" escape="false"/>
								<h:outputText id="participouSid" value=" (#{partSid.participou ? 'PARTICIPOU' : 'NÃO PARTICIPOU'})" /> 
								<h:outputText value="<div id='txaJustificativaSid#{partSid.discenteMonitoria.discente.matricula}' 
								style='display: #{partSid.participou ? 'none':'' }'>"  escape="false"/>
									<f:verbatim><label><i>Justificativa da não participação na elaoração Resumo SID:</i></label><br/></f:verbatim>													
									<h:outputText value="#{partSid.justificativa}"/>									
								<f:verbatim></div></f:verbatim>
						</t:column>
						
					</t:dataTable>
	 		   </td>
		 	</tr>

		</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>