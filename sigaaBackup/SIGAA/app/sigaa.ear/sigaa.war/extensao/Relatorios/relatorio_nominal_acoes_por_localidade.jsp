<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h2>RELAT�RIO GERAL DE EXTENS�O</h2>
	
	<div id="parametrosRelatorio">
	<table>	
	
	
		<tr>
			<th>Local de Realiza��o:</th> 
			<td><h:outputText value="#{relatoriosAtividades.localRealizacao.municipio.nome}"/></td>
		</tr>
	
						
		<tr>
			<th>A��es com Situa��o:</th> 
			<td><h:outputText value="#{relatoriosAtividades.situacaoAtividade.descricao}"/></td>
		</tr>
		
		<tr>
			<th>Realizadas no per�odo de:</th> 
			<td><h:outputText value="#{relatoriosAtividades.dataInicio}">
					<f:convertDateTime pattern="dd/MM/yyyy" />
				</h:outputText>
			 a  <h:outputText value="#{relatoriosAtividades.dataFim}">
			 		<f:convertDateTime pattern="dd/MM/yyyy" />
				</h:outputText>
			</td>
		</tr>		
	</table>
	
	</div>
	<br />
	
	<h3 class="tituloTabelaRelatorio"> Relat�rio Nominal de A��es por Local de Realiza��o ( ${ fn:length(relatoriosAtividades.atividadesLocalizadas) } A��es Encontradas )</h3>

	<table class="tabelaRelatorio" width="100%">
		
		<thead>
			<tr>
				<th>C�digo</th>
				<th>T�tulo</th>				
				<th>Coordenador(a)</th>
			</tr>
		</thead>
		
		<tbody>
		
		<c:forEach items="#{relatoriosAtividades.atividadesLocalizadas}" var="atv">
			<tr class="componentes">
			
				<td width="10%"> ${atv.codigo }</td>
				<td > ${atv.titulo }</td>
				<td >
					<c:if test="${ not empty atv.projeto.coordenador.pessoa.nome}"> 
						
						${atv.projeto.coordenador.pessoa.nome } <br/>
						
						Email:  <c:if test="${ not empty atv.projeto.coordenador.pessoa.email}"> ${atv.projeto.coordenador.pessoa.email } <br/> </c:if>
								<c:if test="${ empty atv.projeto.coordenador.pessoa.email}"> Email N�o Cadastrado <br/> </c:if>
						
						Telefone:   <c:if test="${ not empty atv.projeto.coordenador.pessoa.telefone}"> ${atv.projeto.coordenador.pessoa.telefone } <br/> </c:if>
									<c:if test="${ empty atv.projeto.coordenador.pessoa.telefone}"> Telefone N�o Cadastrado <br/> </c:if>
						
					</c:if>
					
					<c:if test="${ empty atv.projeto.coordenador.pessoa.nome}"> 
						<b>A��O DE EXTENS�O SEM COORDENADOR</b>
					</c:if>
					
				</td>
								
			</tr>
		</c:forEach>
		
		</tbody>		
		
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>