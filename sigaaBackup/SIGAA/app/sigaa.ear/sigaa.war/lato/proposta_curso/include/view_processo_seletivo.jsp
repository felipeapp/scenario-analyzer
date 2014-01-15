<style>
table tbody tr th, table th {
	width: 30%;
}
</style>
		 <caption>Dados do Processo Seletivo</caption>
		  <tr>
  				<th width="30px;">Forma de Seleção:</th>
				<c:choose>
					<c:when test="${ not empty cursoLatoMBean.obj.propostaCurso.formasSelecaoProposta }">
						<c:forEach items="#{cursoLatoMBean.obj.propostaCurso.formasSelecaoProposta}" var="selecao">
							<tr>
								<th></th>
								<td>${selecao.formaSelecao.descricao}</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<td width="70%"></td>
					</c:otherwise>
				</c:choose>
		  </tr>
		  <tr>
				<th width="30px;">Forma de Avaliação:</th>
					<c:choose>
						<c:when test="${ not empty cursoLatoMBean.obj.propostaCurso.formasAvaliacaoProposta }">
							<c:forEach items="#{cursoLatoMBean.obj.propostaCurso.formasAvaliacaoProposta}" var="avaliacao">
								<tr>
									<th></th>
									<td>${avaliacao.formaAvaliacao.descricao}</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<td width="70%"></td>
						</c:otherwise>
					</c:choose>
  		  </tr>
  		  
  		  <tr>
	  		  <c:choose>
	  		  	<c:when test="${ cursoLatoMBean.obj.propostaCurso.nota }">
  		  			<th>Nota Mínima Aprovação: </th>
	  		  		<td> ${cursoLatoMBean.obj.propostaCurso.mediaMinimaAprovacaoDesc} </td>
	  		  	</c:when>
	  		  	
	  		  	<c:otherwise>
	  		  		<th>Conceito Mínimo Aprovação: </th>
  		  			<td> ${cursoLatoMBean.obj.propostaCurso.mediaMinimaAprovacaoDesc} </td>	  		  		
	  		  	</c:otherwise>
	  		  </c:choose>
  		  </tr>