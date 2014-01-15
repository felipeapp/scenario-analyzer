<style>

	#panelNeeContentDiv {
		text-align:left;
	}
	
	#panelNeeContentDiv h1 {
		font-size:9pt;
		font-weight:bold;
		padding:3px;
		background:#DFE8F6;
		margin-top:20px;
		margin-bottom:10px;
	}
</style>

<%-- Botão que exibe o painel do Parecer de NEE --%>
<h:commandButton id="bVisualizarParecerNee" style="display:none;">
	<rich:componentControl for="panelNee" attachTo="bVisualizarParecerNee" operation="show" event="onclick" />
</h:commandButton>

<%-- PARECER_NEE --%>
<rich:modalPanel id="panelNee" autosized="true" minWidth="500" styleClass="panelPerfil">
	<f:facet name="header">
		<h:panelGroup>
			<h:outputText value="PARECER SOBRE A NECESSIDADE EDUCACIONAL ESPECIAL DO DISCENTE" />
		</h:panelGroup>
	</f:facet>
	<f:facet name="controls">
		<h:panelGroup>
			<h:graphicImage value="/img/close.png" styleClass="hidelink" id="hidelink_parecer" />
			<rich:componentControl for="panelNee" attachTo="hidelink_parecer" operation="hide" event="onclick" />
		</h:panelGroup>
	</f:facet>

	<div style="width:100%; height: 350px; overflow: auto;">
		<table width="100%">
			<tr>
				
				<td>
					<strong style="font-weight:bold;">${turmaVirtual.parecerNeeDiscenteSelecionado.discente.pessoa.nome}</strong>
					<h1>Dados do Discente</h1>
					<strong style="font-weight:bold;">Curso:</strong> <em>${turmaVirtual.parecerNeeDiscenteSelecionado.discente.curso.descricao} </em><br/>
					<strong style="font-weight:bold;">Matrícula:</strong> <em>${turmaVirtual.parecerNeeDiscenteSelecionado.discente.matricula}</em> <br/>
					<strong style="font-weight:bold;">Usuário:</strong> <em>${ turmaVirtual.parecerNeeDiscenteSelecionado.discente.usuario != null ? turmaVirtual.parecerNeeDiscenteSelecionado.discente.usuario.login : "Sem cadastro no sistema" } </em><br/>
					<strong style="font-weight:bold;">E-mail:</strong> <em>${ turmaVirtual.parecerNeeDiscenteSelecionado.discente.pessoa.email != null ? turmaVirtual.parecerNeeDiscenteSelecionado.discente.pessoa.email : "Desconhecido" }</em>
				</td>
			</tr>
			<tr>
				<td>
					<h1>Tipo de Necessidade Educacional Especial </h1>
		 			<c:forEach items="${turmaVirtual.parecerNeeDiscenteSelecionado.tiposNecessidadeSolicitacaoNee}" var="item">
						<strong style="font-weight:bold;">${item.tipoNecessidadeEspecial.descricao}</strong> <br/>
					</c:forEach>
				</td>
			</tr>	
			<tr>
				<td> 
					<h1>Parecer da CAENE a respeito da Necessidade Educacional do Aluno</h1>
					<h:outputText value="#{turmaVirtual.parecerNeeDiscenteSelecionado.parecerComissao}" id="parecerComissao" />
					<br/><br/>
				</td>
			</tr>
			<tr>
				<td>
					<a href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>,'${solicitacaoApoioNee.emailCaene}');">
						<img src="${ctx}/img/email_go.png" alt="Enviar Mensagem Para a CAENE" title="Enviar Mensagem Para a CAENE"/> Enviar Mensagem para CAENE 
					</a>
				</td>	
			</tr>	
		</table>
	</div>
	<div style="width:100%;text-align:center;height:25px;background:#DFE8F6;vertical-align:middle;padding-top:5px;">
		<h:commandButton id="bFecharNee" value="Fechar">
	    	<rich:componentControl for="panelNee" attachTo="bFecharNee" operation="hide" event="onclick" />
	    </h:commandButton>
	   
			
		
	</div>

</rich:modalPanel>

