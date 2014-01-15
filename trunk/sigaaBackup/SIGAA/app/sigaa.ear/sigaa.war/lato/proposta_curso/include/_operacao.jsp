	<div id="ajuda" class="descricaoOperacao" style="text-align: justify;">
		<div style="float: left; width: 50%; border: 0; position: relative;">
			Passo atual do cadastro da proposta de Curso Lato Sensu. <br />
		</div>
		<div style="float: left; width: 50%; border: 0; position: relative;">
			<c:forEach var="passo" items="${cursoLatoMBean.obj.tipoPassoPropostaLato.all}" varStatus="loop">
				<c:choose>
					<c:when test="${ cursoLatoMBean.obj.tipoPassoPropostaLato == passo}">
						<b>${loop.index + 1 }. ${passo.label}</b><br />
					</c:when>
					<c:otherwise>
						   ${loop.index + 1 }. ${passo.label}<br />
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
		<br clear="all"/>
	</div>