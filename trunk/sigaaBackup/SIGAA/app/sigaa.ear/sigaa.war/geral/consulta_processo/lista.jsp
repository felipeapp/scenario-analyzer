<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
	function popup(id){
		window.open('${ configSistema['linkSipac'] }/public/jsp/processos/processo_detalhado.jsf?id=' + id,'popup','width=970,height=600,scrollbars=yes')
	}
</script>

<f:view>
	<h2><ufrn:subSistema/> &gt; Lista de Processo</h2>
	
	<div id="ajuda" class="descricaoOperacao">
		<p><strong>Caro Aluno,</strong></p>
		<p>
			O sistema busca todos os processos que estejam associados a todos os vínculos que você possui, ou seja:
			<ul>
				<li>
					Caso tenha mais de um vínculo de discente, não importa com qual vínculo esteja logado, todos os processos serão exibidos nessa página.
				</li>
				<li>
					Caso possua processos cadastrado para servidor ou pessoa física eles também serão exibidos, mesmo você logado como discente.
				</li>
			</ul> 
		</p>	
	</div>	
	
	<center>
		<h:messages/>
		<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Processo
		</div>
	</center>
	
	<table class="listagem" style="width: 100%">
		<caption>Lista de Processos</caption>
		<thead>
			<tr>
				<td>Nº Processo</td>
				<td>Assunto</td>
				<td>Origem</td>
				<td></td>
			</tr>
		</thead>
		<c:forEach items="${consultaProcesso.conjunto}" var="processo" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${processo.numProtocoloCompleto}</td>
				<td>${processo.assunto}</td>
				<td>${processo.unidadeOrigem}</td>
				<h:form>
					<td width=20><a href="#" onclick="javascript:popup(${processo.id})"><h:graphicImage value="/img/view.gif"style="overflow: visible;"/></a> </td>
				</h:form>
				
			</tr>
		</c:forEach>
	</table>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>