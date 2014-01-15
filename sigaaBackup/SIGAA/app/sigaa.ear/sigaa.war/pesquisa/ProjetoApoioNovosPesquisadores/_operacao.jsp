	<div id="ajuda" class="descricaoOperacao" style="text-align: justify;">
		<div style="float: left; width: 50%; border: 0; position: relative;">
			Passo atual do cadastro do Projeto de Apoio a Novos Pesquisadores <br />
		</div>
		<div style="float: left; width: 50%; border: 0; position: relative;">
			<c:forEach var="passo" items="${ projetoApoioNovosPesquisadoresMBean.obj.tipoPassosProjeto.all }" varStatus="loop">
				<c:choose>
					<c:when test="${ projetoApoioNovosPesquisadoresMBean.obj.tipoPassosProjeto == passo}">
						<b>${loop.index + 1 }. ${ passo.label }</b><br />
					</c:when>
					<c:otherwise>
						   ${loop.index + 1 }. ${ passo.label }<br />
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
		<br clear="all"/>
		<ul>
			<li> No cadastro do projeto de Apoio a Novos Pesquisadores a cada tela que é avançada o sistema vai salvando automaticamente os dados inseridos, 
				porém é necessário que ao final o usuário submeta o projeto. </li>
		</ul>
		
	</div>